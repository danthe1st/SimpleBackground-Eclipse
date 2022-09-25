package io.github.danthe1st.simple_background_eclipse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import io.github.danthe1st.simple_background_eclipse.preferences.PreferenceConstants;

public class SimpleBackgroundPlugin extends Plugin {
	
	private static final Bundle BUNDLE = FrameworkUtil.getBundle(SimpleBackgroundPlugin.class);
	
	private static SimpleBackgroundPlugin instance;
	
	private PreferenceStore preferenceStore;
	
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
		PlatformUI.getWorkbench().getDisplay().asyncExec(() -> {
			reloadBackground();
			getRootShell().addControlListener(new ControlListener() {
				
				@Override
				public void controlResized(ControlEvent e) {
					reloadBackground();
				}
				
				@Override
				public void controlMoved(ControlEvent e) {
					
				}
			});
		});
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
		Shell shell = getRootShell();
		reloadBackground(shell);
	}
	
	private void reloadBackground(Shell shell) {
		String bgImagePath = preferenceStore.getString(PreferenceConstants.BACKGROUND_IMAGE_PATH);
		if(bgImagePath != null && !bgImagePath.isBlank()){
			Image image = new Image(Display.getDefault(), bgImagePath);
			Point size = shell.getSize();
			int width = size.x;
			int height = size.y;
			
			Image scaled = new Image(Display.getDefault(), width, height);
			GC gc = new GC(scaled);
			gc.setAntialias(SWT.ON);
			gc.setInterpolation(SWT.HIGH);
			gc.setBackground(new Color(0, 0, 0));
			double scalingFactor = Math.max(width / image.getBounds().width, height / image.getBounds().height);
			
			gc.setAlpha(preferenceStore.getInt(PreferenceConstants.BACKGROUND_IMAGE_ALPHA));
			gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, 0, 0, (int) (image.getBounds().width * scalingFactor), (int) (image.getBounds().height * scalingFactor));
			
			gc.dispose();
			image.dispose();
			
			Image oldBackgroundImage = shell.getBackgroundImage();
			shell.setBackgroundImage(scaled);
			if(oldBackgroundImage != null){
				oldBackgroundImage.dispose();
			}
			shell.setBackgroundMode(SWT.INHERIT_FORCE);
		}else{
			shell.setBackgroundImage(null);
		}
		
		shell.setAlpha(preferenceStore.getInt(PreferenceConstants.BACKGROUND_ALPHA));
	}
	
	private Shell getRootShell() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		Shell shell = workbench.getDisplay().getActiveShell();
		Composite composite = shell;
		do{
			shell = composite.getShell();
			composite = composite.getParent();
		}while(composite != null);
		return shell;
	}
	
}
