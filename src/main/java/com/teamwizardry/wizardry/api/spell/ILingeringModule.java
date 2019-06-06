package com.teamwizardry.wizardry.api.spell;

import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque.
 */
public interface ILingeringModule {

	int getLingeringTime(World world, SpellData spell, SpellRing spellRing);

	/**
	 * Runs once when lingering starts
	 */
	default boolean runOnStart(@Nonnull World world, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		return true;
	}

	/**
	 * Runs once when lingering ends
	 */
	default boolean runOnFinish(@Nonnull World world, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		return true;
	}
}
