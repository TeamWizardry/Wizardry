package com.teamwizardry.wizardry.api.spell;

import javax.annotation.Nonnull;

/**
 * Created by LordSaad.
 */
public interface IOverrideCooldown {

	int getNewCooldown(@Nonnull SpellData data);
}
