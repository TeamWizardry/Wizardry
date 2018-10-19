package com.teamwizardry.wizardry.api.spell.module;

import javax.annotation.Nonnull;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;

public interface IRenderableModule<InstanceType extends Module> {
	
	/**
	 * Will render whatever GL code is specified here while the spell is being held by the
	 * player's hand.
	 */
	default SpellData renderVisualization(InstanceType instance, @Nonnull SpellData data, @Nonnull SpellRing ring, @Nonnull SpellData previousData) {
		return instance.standardRenderVisualization(data, ring, previousData);
	}

	/**
	 * This method runs client side when the spellData runs. Spawn particles here.
	 */
	default void renderSpell(InstanceType instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		
	}
}
