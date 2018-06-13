package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.wizardry.common.core.SpellTicker;

/**
 * Created by Demoniaque.
 */
public interface IDelayedModule {

	void runDelayedEffect(SpellData spell, SpellRing spellRing);

	default void addDelayedSpell(SpellRing spellRing, SpellData data, int expiry) {
		SpellTicker.addDelayedSpell(this, spellRing, data, expiry);
	}
}
