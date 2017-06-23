package com.teamwizardry.wizardry.api.spell.module;

import com.teamwizardry.wizardry.api.spell.ITaxing;

public abstract class ModuleEffect extends Module implements ITaxing
{
	@Override
	public ModuleType getModuleType()
	{
		return ModuleType.EFFECT;
	}
}
