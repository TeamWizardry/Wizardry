package com.teamwizardry.wizardry.api.spell.module;

import com.teamwizardry.wizardry.api.spell.ICostModifier;

public abstract class ModuleShape extends Module implements ICostModifier
{
	@Override
	public ModuleType getModuleType()
	{
		return ModuleType.SHAPE;
	}
}
