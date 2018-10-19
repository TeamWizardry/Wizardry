package com.teamwizardry.wizardry.api.spell.module;

import javax.annotation.Nonnull;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;

public interface IRunnableModule<InstanceType extends Module> {
	/**
	 * Only return false if the spellData cannot be taxed from mana. Return true otherwise.
	 */
	boolean run(InstanceType instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing);
}
