package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.wizardry.api.spell.module.ModuleInstance;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque.
 */
public interface ILingeringModule {

	int getLingeringTime(SpellData spell, SpellRing spellRing);

	/**
	 * Runs once when lingering starts
	 */
	default boolean runOnce(ModuleInstance instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		return true;
	}
}
