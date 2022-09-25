package io.github.danthe1st.simple_background_eclipse.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import io.github.danthe1st.simple_background_eclipse.SimpleBackgroundPlugin;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {
	
	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = SimpleBackgroundPlugin.getInstance().getPreferenceStore();
		store.setDefault(PreferenceConstants.BACKGROUND_ALPHA, 255);
		store.setDefault(PreferenceConstants.BACKGROUND_IMAGE_PATH, "");
	}
	
}
