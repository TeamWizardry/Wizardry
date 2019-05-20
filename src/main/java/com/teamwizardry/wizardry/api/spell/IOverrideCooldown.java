package com.teamwizardry.wizardry.api.spell;

import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque.
 */
public interface IOverrideCooldown {

	int getNewCooldown(World world, @Nonnull SpellData spell, SpellRing ring);
}
