package com.teamwizardry.wizardry.api.spell.module;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * Interface for module features related to execution. Event modules are also using this interface. <br />
 * <b>NOTE</b>: Shouldn't be derived directly from. Instead use one of interface derivatives from same package.
 * 
 * @author Avatair
 *
 * @param <InstanceType> the associated instance class.
 */
public interface IRunnableModule<InstanceType extends ModuleInstance> {
	/**
	 * Only return false if the spellData cannot be taxed from mana. Return true otherwise.
	 */
	boolean run(@NotNull World world, InstanceType instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing);
	
	/**
	 * If children should be traversed after execution by the standard Spell Ring execution.
	 * 
	 * @return <code>true</code> iff yes.
	 */
	default boolean shouldRunChildren() {
		return true;
	}
}
