package com.teamwizardry.wizardry.api.spell.module;

import javax.annotation.Nonnull;

public abstract class ModuleShape extends Module {
	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.SHAPE;
	}
}
