package io.github.danthe1st.simple_background_eclipse.preferences;

public enum TransparencyMode {
	ALL_SHELLS("all windows"),
	ACTIVE_SHELL("active window only"),
	ACTIVE_ROOT_SHELL("root window only"),
	ALL_EXCEPT_ACTIVE_ROOT_SHELL("allow windows except root window"),
	INACTIVE_SHELLS("inactive windows");
	
	private final String modeText;
	
	TransparencyMode(String modeText) {
		this.modeText = modeText;
	}
	
	public String getModeText() {
		return modeText;
	}
}
