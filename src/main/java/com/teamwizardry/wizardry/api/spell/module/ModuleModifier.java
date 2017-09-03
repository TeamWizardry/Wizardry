package com.teamwizardry.wizardry.api.spell.module;

import javax.annotation.Nonnull;

public abstract class ModuleModifier extends Module {
	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.MODIFIER;
	}

	public abstract void apply(@Nonnull Module module);
}
