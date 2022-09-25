package io.github.danthe1st.simple_background_eclipse;

import org.eclipse.ui.IStartup;

public class SimpleBackgroundStartup implements IStartup {
	
	@Override
	public void earlyStartup() {
		SimpleBackgroundPlugin.getInstance();
	}
	
}
