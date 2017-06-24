package com.teamwizardry.wizardry.api.spell.module;

import org.jetbrains.annotations.NotNull;

public abstract class ModuleEvent extends Module
{
	@NotNull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EVENT;
	}
}
