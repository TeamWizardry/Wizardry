package com.teamwizardry.wizardry.api.spell.module;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public abstract class ModuleModifier extends Module {
	@NotNull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.MODIFIER;
	}

	public abstract void apply(@Nonnull Module module);
}
