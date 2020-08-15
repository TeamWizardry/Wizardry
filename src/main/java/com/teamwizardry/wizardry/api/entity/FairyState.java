package com.teamwizardry.wizardry.api.entity;

import com.teamwizardry.wizardry.api.StringConsts;

import javax.annotation.Nonnull;

/// TODO move to common package. We're abstracting the fairy part of this system away and making it more general for reuse potential.
public enum FairyState {

	HAPPY(StringConsts.HAPPY), TRAPPED(StringConsts.TRAPPED), DEPRESSED(StringConsts.DEPRESSED), SLAVED(StringConsts.SLAVED);

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
			case StringConsts.HAPPY:
				return HAPPY;
			case StringConsts.TRAPPED:
				return TRAPPED;
			case StringConsts.DEPRESSED:
				return DEPRESSED;
			case StringConsts.SLAVED:
				return SLAVED;
		}
		return HAPPY;
	}
}
