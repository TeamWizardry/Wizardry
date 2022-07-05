package com.teamwizardry.wizardry.api.spell.module;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import net.minecraft.world.World;

/**
 * Interface for event modules. <br/>
 * 
 * <b>Usage</b>: Override {@link IRunnableModule#run(World, ModuleInstance, SpellData, SpellRing)} and return <code>true</code> iff following elements from a spell chain must be executed.
 * 
 * @author Avatair
 */
public interface IModuleEvent extends IModule, IRunnableModule<ModuleInstanceEvent> {

}
