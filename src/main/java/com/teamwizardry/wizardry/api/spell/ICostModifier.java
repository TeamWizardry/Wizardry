package com.teamwizardry.wizardry.api.spell;

/**
 * Created by LordSaad.
 */
public interface ICostModifier {

	default void setCostMultiplier(Module module, double multiplier) {
		module.setMultiplier(module.getMultiplier() * multiplier);
	}
}
