package com.teamwizardry.wizardry.api.spell.module;

import com.teamwizardry.wizardry.api.spell.ICostModifier;

import javax.annotation.Nonnull;

public abstract class ModuleShape extends Module implements ICostModifier
{
	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.SHAPE;
	}
}
