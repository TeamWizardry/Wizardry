package com.teamwizardry.wizardry.api.spell;

import javax.annotation.Nonnull;

/**
 * Created by LordSaad.
 */
public interface IModifier {

	void apply(@Nonnull Module module);

	double costMultiplier();
}
