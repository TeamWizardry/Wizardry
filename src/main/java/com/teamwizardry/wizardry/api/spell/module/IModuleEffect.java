package com.teamwizardry.wizardry.api.spell.module;

/**
 * Interface for effect modules.
 * 
 * @author Avatair
 */
public interface IModuleEffect extends IModule, IRenderableModule<ModuleInstanceEffect>, IRunnableModule<ModuleInstanceEffect> {

	default void initEffect(ModuleInstanceEffect instance) {
	}

}
