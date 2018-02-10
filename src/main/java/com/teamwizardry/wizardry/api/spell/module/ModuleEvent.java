package com.teamwizardry.wizardry.api.spell.module;

import javax.annotation.Nonnull;

public abstract class ModuleEvent extends Module {
	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EVENT;
	}
}
