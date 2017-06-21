package com.teamwizardry.wizardry.api.spell;

public abstract class ModuleEvent extends Module
{
	@Override
	public ModuleType getModuleType()
	{
		return ModuleType.EVENT;
	}
}
