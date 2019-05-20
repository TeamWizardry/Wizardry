package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.wizardry.api.SpellObjectManager;
import com.teamwizardry.wizardry.api.capability.world.WizardryWorld;
import com.teamwizardry.wizardry.api.capability.world.WizardryWorldCapability;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque.
 */
public interface IDelayedModule {

	void runDelayedEffect(@Nonnull World world, SpellData spell, SpellRing spellRing);

	default void addDelayedSpell(@Nonnull World world, SpellRing spellRing, SpellData data, int expiry) {
		WizardryWorld worldCap = WizardryWorldCapability.get(world);
		worldCap.getSpellObjectManager().addDelayed(new SpellObjectManager.DelayedObject(world, data, spellRing), expiry);
	}
}
