package com.teamwizardry.wizardry.api.spell.module;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Interface for module features related to rendering. <br />
 * <b>NOTE</b>: Shouldn't be derived directly from. Instead use one of interface derivatives from same package.
 * 
 * @author Avatair
 *
 * @param <InstanceType> the associated instance class.
 */
public interface IRenderableModule<InstanceType extends ModuleInstance> {
	
	/**
	 * Will render whatever GL code is specified here while the spell is being held by the
	 * player's hand.
	 */
	default SpellData renderVisualization(@Nonnull World world, InstanceType instance, @Nonnull SpellData data, @Nonnull SpellRing ring, float partialTicks) {
		return instance.standardRenderVisualization(data, ring, partialTicks);
	}

	/**
	 * This method runs client side when the spellData runs. Spawn particles here.
	 */
	default void renderSpell(World world, InstanceType instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		
	}
	
	/**
	 * If module shouldn't be rendered when executing.
	 * 
	 * @return <code>true</code> iff yes.
	 */
	default boolean ignoreResultsForRendering() {
		return false;
	}
}
