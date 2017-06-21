package com.teamwizardry.wizardry.api.spell;

public abstract class ModuleShape extends Module implements ICostModifier
{
	@Override
	public ModuleType getModuleType()
	{
		return ModuleType.SHAPE;
	}
}
