package com.teamwizardry.wizardry.api.entity;

import javax.annotation.Nonnull;

public enum FairyState {

	HAPPY(Constants.HAPPY), TRAPPED(Constants.TRAPPED), DEPRESSED(Constants.DEPRESSED), SLAVED(Constants.SLAVED);

	public final String nbt;

	FairyState(String nbt) {
		this.nbt = nbt;
	}

	/**
	 * @param nbt And nbt value matching one of the Constants
	 * @return Returns HAPPY if the state doesn't exist.
	 */
	@Nonnull
	public static FairyState fromNbt(String nbt) {
		switch (nbt) {
			case Constants.HAPPY:
				return HAPPY;
			case Constants.TRAPPED:
				return TRAPPED;
			case Constants.DEPRESSED:
				return DEPRESSED;
			case Constants.SLAVED:
				return SLAVED;
		}
		return HAPPY;
	}

	private static class Constants {
		private static final String HAPPY = "happy";
		private static final String TRAPPED = "trapped";
		private static final String DEPRESSED = "depressed";
		private static final String SLAVED = "slaved";
	}
}
