package com.teamwizardry.wizardry.api.spell.module;

/**
 * An exception which is thrown in case a module couldn't be initialized properly.
 * 
 * @author Avatair
 */
public class ModuleInitException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2993076011209575574L;

	public ModuleInitException(String message, Throwable cause) {
		super(message, cause);
	}

	public ModuleInitException(String message) {
		super(message);
	}

}
