package com.teamwizardry.wizardry.api.spell.module;

public abstract class ModuleEvent extends Module
{
	@Override
	public ModuleType getModuleType()
	{
		return ModuleType.EVENT;
	}
}
