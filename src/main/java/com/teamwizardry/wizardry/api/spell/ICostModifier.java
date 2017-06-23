package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.wizardry.api.spell.module.Module;

/**
 * Created by LordSaad.
 */
public interface ICostModifier {

	default void setCostMultiplier(Module module, double multiplier) {
		module.setMultiplier(module.getMultiplier() * multiplier);
	}
}
