package com.teamwizardry.wizardry.api.spell;

public class DataSerializationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4279965619028882088L;

	public DataSerializationException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataSerializationException(String message) {
		super(message);
	}

}
