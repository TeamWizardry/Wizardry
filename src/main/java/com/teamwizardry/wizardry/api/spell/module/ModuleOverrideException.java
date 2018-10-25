package com.teamwizardry.wizardry.api.spell.module;

public class ModuleOverrideException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6385982403711483432L;

	public ModuleOverrideException(String message, Throwable cause) {
		super(message, cause);
	}

	public ModuleOverrideException(String message) {
		super(message);
	}

}
