package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.wizardry.api.capability.world.WizardryWorld;
import com.teamwizardry.wizardry.api.capability.world.WizardryWorldCapability;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstance;

/**
 * Created by Demoniaque.
 */
public interface IDelayedModule {

	void runDelayedEffect(SpellData spell, SpellRing spellRing);

	default void addDelayedSpell(ModuleInstance module, SpellRing spellRing, SpellData data, int expiry) {
		WizardryWorld worldCap = WizardryWorldCapability.get(data.world);
		worldCap.addDelayedSpell(module, spellRing, data, expiry);
	}
}
