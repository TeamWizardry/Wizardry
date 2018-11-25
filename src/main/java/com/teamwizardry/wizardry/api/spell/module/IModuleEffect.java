package com.teamwizardry.wizardry.api.spell.module;

/**
 * Interface for effect modules.
 * 
 * @author Avatair
 */
public interface IModuleEffect extends IModule, IRenderableModule<ModuleInstanceEffect>, IRunnableModule<ModuleInstanceEffect> {

	/**
	 * Overrideable method to add more initializations for the effect. <br/>
	 * <b>NOTE</b>: Is called whenever the module class is instanced.
	 * 
	 * @param instance the effect module.
	 */
	default void initEffect(ModuleInstanceEffect instance) {
	}

}
