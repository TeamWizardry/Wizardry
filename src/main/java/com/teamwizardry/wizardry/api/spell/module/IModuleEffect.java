package com.teamwizardry.wizardry.api.spell.module;

public interface IModuleEffect extends IModule, IRenderableModule<ModuleInstanceEffect>, IRunnableModule<ModuleInstanceEffect> {

	default void initEffect(ModuleInstanceEffect instance) {
	}

}
