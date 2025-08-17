package io.github.danthe1st.simple_background_eclipse.preferences;

import java.io.IOException;

import io.github.danthe1st.simple_background_eclipse.SimpleBackgroundPlugin;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class MainPreferencePage
		extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {
	
	public MainPreferencePage() {
		super(GRID);
		setPreferenceStore(SimpleBackgroundPlugin.getInstance().getPreferenceStore());
		setDescription("A demonstration of a preference page implementation");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI
	 * blocks needed to manipulate various types of preferences. Each field editor
	 * knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {
		FileFieldEditor backgroundImageEditor = new FileFieldEditor(
				PreferenceConstants.BACKGROUND_IMAGE_PATH,
				"&Background image:", getFieldEditorParent()
		);
		backgroundImageEditor.setFileExtensions(new String[] { "*.png", "*.jpg", "*.gif" });
		IntegerFieldEditor alphaEditor = new IntegerFieldEditor(
				PreferenceConstants.BACKGROUND_ALPHA,
				"&Background alpha",
				getFieldEditorParent()
		);
		IntegerFieldEditor imageAlphaEditor = new IntegerFieldEditor(
				PreferenceConstants.BACKGROUND_IMAGE_ALPHA,
				"&Background image alpha",
				getFieldEditorParent()
		);
		
		TransparencyMode[] transparencyModes = TransparencyMode.values();
		String[][] transparencyModeChoices = new String[transparencyModes.length][];
		for(int i = 0; i < transparencyModes.length; i++){
			TransparencyMode mode = transparencyModes[i];
			transparencyModeChoices[i] = new String[] { mode.getModeText(), mode.name() };
		}
		
		ComboFieldEditor transparencyModeEditor = new ComboFieldEditor(
			PreferenceConstants.TRANSPARENCY_MODE,
			"&Use transparency for these windows",
			transparencyModeChoices,
			getFieldEditorParent()
		);
		
		ComboFieldEditor backgroundImageModeEditor = new ComboFieldEditor(
			PreferenceConstants.BACKGROUND_IMAGE_TRANSPARENCY_MODE,
			"&Use background image for these windows",
			transparencyModeChoices,
			getFieldEditorParent()
		);
			
		alphaEditor.setValidRange(200, 255);
		imageAlphaEditor.setValidRange(0, 255);
		addField(backgroundImageEditor);
		addField(alphaEditor);
		addField(imageAlphaEditor);
		addField(transparencyModeEditor);
		addField(backgroundImageModeEditor);
		
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
	}
	
	@Override
	public boolean performOk() {
		boolean ret = super.performOk();
		try{
			SimpleBackgroundPlugin.getInstance().savePreferences();
		}catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleBackgroundPlugin.getInstance().reloadBackground();
		return ret;
	}
}