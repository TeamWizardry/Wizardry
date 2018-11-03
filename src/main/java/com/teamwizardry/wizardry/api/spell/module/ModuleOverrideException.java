package com.teamwizardry.wizardry.api.spell.module;

/**
 * Exception which is thrown when calling an override method.
 * 
 * @author Avatair
 */
public class ModuleOverrideException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6385982403711483432L;

	/**
	 * The constructor.
	 * 
	 * @param message the exception message.
	 * @param cause the exception cause.
	 */
	public ModuleOverrideException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * The constructor.
	 * 
	 * @param message the exception message.
	 */
	public ModuleOverrideException(String message) {
		super(message);
	}

}
