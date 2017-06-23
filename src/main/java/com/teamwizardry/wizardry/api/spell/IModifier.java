package com.teamwizardry.wizardry.api.spell;

import javax.annotation.Nonnull;

import com.teamwizardry.wizardry.api.spell.module.Module;

/**
 * Created by LordSaad.
 */
public interface IModifier {

	void apply(@Nonnull Module module);
}
