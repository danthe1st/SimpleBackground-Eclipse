package io.github.danthe1st.simple_background_eclipse.preferences;

import io.github.danthe1st.simple_background_eclipse.SimpleBackgroundPlugin;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

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
		store.setDefault(PreferenceConstants.TRANSPARENCY_MODE, TransparencyMode.ALL_SHELLS.name());
		store.setDefault(PreferenceConstants.BACKGROUND_IMAGE_TRANSPARENCY_MODE, TransparencyMode.ALL_SHELLS.name());
	}
	
}
