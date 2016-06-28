package com.teamwizardry.wizardry.api.gui;

public enum EnumMouseButton {

	LEFT, RIGHT, MIDDLE,
	BUTTON3, BUTTON4, BUTTON5, BUTTON6, BUTTON7, BUTTON8, BUTTON9, BUTTON10, BUTTON11, BUTTON12, BUTTON13, BUTTON14, BUTTON15, BUTTON16,
	UNKNOWN;
	
	public static EnumMouseButton getFromCode(int code) {
		if(code < 0 || code >= values().length) {
			return UNKNOWN;
		}
		return values()[code];
	}
	
	public int getMouseCode() {
		return this == UNKNOWN ? -1 : ordinal();
	}
	
}
