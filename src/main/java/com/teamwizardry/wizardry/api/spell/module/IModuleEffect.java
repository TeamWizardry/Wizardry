package com.teamwizardry.wizardry.api.spell.module;

public interface IModuleEffect extends IModule, IRenderableModule<ModuleEffect>, IRunnableModule<ModuleEffect> {

	default void initEffect(ModuleEffect instance) {
	}

}
