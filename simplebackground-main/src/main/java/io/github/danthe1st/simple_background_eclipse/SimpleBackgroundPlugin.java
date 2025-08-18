package io.github.danthe1st.simple_background_eclipse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import io.github.danthe1st.simple_background_eclipse.preferences.PreferenceConstants;
import io.github.danthe1st.simple_background_eclipse.preferences.TransparencyMode;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

public class SimpleBackgroundPlugin extends Plugin {
	
	private static final Bundle BUNDLE = FrameworkUtil.getBundle(SimpleBackgroundPlugin.class);
	
	private static SimpleBackgroundPlugin instance;
	
	private PreferenceStore preferenceStore;
	private Map<Shell, Image> activeBackgroundImages = new HashMap<>();
	
	public static SimpleBackgroundPlugin getInstance() {
		synchronized(SimpleBackgroundPlugin.class){
			if(instance == null){
				throw new IllegalStateException("Plugin not yet initialized");
			}
			return instance;
		}
	}
	
	public SimpleBackgroundPlugin() throws IOException {
		synchronized(SimpleBackgroundPlugin.class){
			if(instance != null){
				throw new IllegalStateException("Cannot instantiate plugin class multiple times");
			}
			instance = this;
		}
		Path path = BUNDLE.getDataFile("simplebackground.preferences").toPath();
		if(!Files.exists(path)){
			Files.createFile(path);
		}
		
		preferenceStore = new PreferenceStore(path.toString());
		preferenceStore.setDefault(PreferenceConstants.BACKGROUND_ALPHA, 255);
		preferenceStore.setDefault(PreferenceConstants.BACKGROUND_IMAGE_ALPHA, 255);
		preferenceStore.load();
		
		PlatformUI.getWorkbench().getDisplay().asyncExec(this::reloadBackground);
	}
	
	private TransparencyMode getBackgroundTransparencyMode() {
		return getTransparencyMode(PreferenceConstants.TRANSPARENCY_MODE);
	}
	
	private TransparencyMode getBackgroundImageTransparencyMode() {
		return getTransparencyMode(PreferenceConstants.BACKGROUND_IMAGE_TRANSPARENCY_MODE);
	}
	
	private TransparencyMode getTransparencyMode(String preferenceConstant) {
		String modeName = preferenceStore.getString(preferenceConstant);
		try{
			return TransparencyMode.valueOf(modeName);
		}catch(IllegalArgumentException e){
			return TransparencyMode.ALL_SHELLS;
		}
	}
	
	private void deactivateShell(Shell shell) {
		executeDeactivateAction(getBackgroundImageTransparencyMode(), shell, this::configureBackground, this::unsetBackground);
		executeDeactivateAction(getBackgroundTransparencyMode(), shell, this::configureAlpha, this::unsetAlpha);
	}
	
	private void activateShell(Shell shell) {
		executeActivateAction(getBackgroundImageTransparencyMode(), shell, this::configureBackground, this::unsetBackground);
		executeActivateAction(getBackgroundTransparencyMode(), shell, this::configureAlpha, this::unsetAlpha);
	}
	
	private void executeActivateAction(TransparencyMode mode, Shell shell, Consumer<Shell> configure, Consumer<Shell> unset) {
		switch(mode) {
		case ALL_SHELLS, ACTIVE_SHELL -> configure.accept(shell);
		case INACTIVE_SHELLS -> unset.accept(shell);
		case ACTIVE_ROOT_SHELL -> configure.accept(getRootShell(shell));
		case ALL_EXCEPT_ACTIVE_ROOT_SHELL -> configure.accept(getRootShell(shell));
		}
	}
	
	private void executeDeactivateAction(TransparencyMode mode, Shell shell, Consumer<Shell> configure, Consumer<Shell> unset) {
		switch(mode) {
		case ACTIVE_SHELL -> unset.accept(shell);
		case ALL_SHELLS, INACTIVE_SHELLS -> configure.accept(shell);
		case ACTIVE_ROOT_SHELL -> configure.accept(getRootShell(shell));
		case ALL_EXCEPT_ACTIVE_ROOT_SHELL -> unset.accept(getRootShell(shell));
		}
	}
	
	@Override
	public synchronized void stop(BundleContext context) throws Exception {
		super.stop(context);
	}
	
	public IPreferenceStore getPreferenceStore() {
		return preferenceStore;
	}
	
	public void savePreferences() throws IOException {
		preferenceStore.save();
	}
	
	public void reloadBackground() {
		Set<Long> knownShellHandles = new HashSet<>();
		PlatformUI.getWorkbench().getDisplay().addFilter(SWT.Activate, e -> {
			if(e.widget instanceof Shell shell){
				activateShell(shell);
				if(knownShellHandles.add(shell.handle)){
					shell.addControlListener(new ControlListener() {
						
						@Override
						public void controlResized(ControlEvent unused) {
							// reset background
							activateShell(shell);
						}
						
						@Override
						public void controlMoved(ControlEvent unused) {
							
						}
					});
					shell.addListener(SWT.Close, unused -> {
						knownShellHandles.remove(shell.handle);
						changeBackgroundImage(shell, null);
					});
				}
			}
		});
		
		PlatformUI.getWorkbench().getDisplay().addFilter(SWT.Deactivate, e -> {
			if(e.widget instanceof Shell shell){
				deactivateShell(shell);
			}
		});
		for(Shell shell : PlatformUI.getWorkbench().getDisplay().getShells()){
			deactivateShell(shell);
		}
		
		activateShell(PlatformUI.getWorkbench().getDisplay().getActiveShell());
	}
	
	private void configureAlpha(Shell shell) {
		shell.setAlpha(preferenceStore.getInt(PreferenceConstants.BACKGROUND_ALPHA));
	}
	
	private void unsetAlpha(Shell shell) {
		shell.setAlpha(255);
	}
	
	private void configureBackground(Shell shell) {
		String bgImagePath = preferenceStore.getString(PreferenceConstants.BACKGROUND_IMAGE_PATH);
		if(bgImagePath != null && !bgImagePath.isBlank()){
			Image image = new Image(Display.getDefault(), bgImagePath);
			Point size = shell.getSize();
			int width = size.x;
			int height = size.y;
			
			Image scaled = new Image(Display.getDefault(), width, height);
			GC gc = new GC(scaled);
			gc.setAntialias(SWT.ON);
			gc.setInterpolation(SWT.LOW);
			gc.setBackground(new Color(0, 0, 0));
			double scalingFactor = Math.max(width / (double) image.getBounds().width, height / (double) image.getBounds().height);
			
			gc.setAlpha(preferenceStore.getInt(PreferenceConstants.BACKGROUND_IMAGE_ALPHA));
			gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, 0, 0, (int) (image.getBounds().width * scalingFactor), (int) (image.getBounds().height * scalingFactor));
			
			gc.dispose();
			image.dispose();
			
			changeBackgroundImage(shell, scaled);
			shell.setBackgroundMode(SWT.INHERIT_FORCE);
		}else{
			unsetBackground(shell);
		}
	}
	
	private void unsetBackground(Shell shell) {
		changeBackgroundImage(shell, null);
	}
	
	private Shell getRootShell(Shell shell) {
		Composite composite = shell;
		do{
			shell = composite.getShell();
			composite = composite.getParent();
		}while(composite != null);
		return shell;
	}
	
	private void changeBackgroundImage(Shell shell, Image newBackgroundImage) {
		Image oldImage = activeBackgroundImages.get(shell);
		if(newBackgroundImage == null){
			activeBackgroundImages.remove(shell);
		}else{
			shell.setBackgroundImage(newBackgroundImage);
			activeBackgroundImages.put(shell, newBackgroundImage);
		}
		if(oldImage != null){
			oldImage.dispose();
		}
	}
}
