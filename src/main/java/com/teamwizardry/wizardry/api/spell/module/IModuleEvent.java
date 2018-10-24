package com.teamwizardry.wizardry.api.spell.module;

/**
 * Interface for event modules. <br/>
 * 
 * <b>Usage</b>: Override {@link IRunnableModule#run()} and return <code>true</code> iff following elements from a spell chain must be executed.
 * 
 * @author Avatair
 */
public interface IModuleEvent extends IModule, IRunnableModule<ModuleInstanceEvent> {

}
