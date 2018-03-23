package com.teamwizardry.wizardry.api.spell;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque.
 */
public interface IOverrideCooldown {

	int getNewCooldown(@Nonnull SpellData spell, SpellRing ring);
}
