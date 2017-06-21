package com.teamwizardry.wizardry.api.spell;

public abstract class ModuleEffect extends Module implements ITaxing
{
	@Override
	public ModuleType getModuleType()
	{
		return ModuleType.EFFECT;
	}
}
