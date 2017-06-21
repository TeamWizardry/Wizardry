package com.teamwizardry.wizardry.api.spell;

public abstract class ModuleModifier extends Module implements IModifier
{
	@Override
	public ModuleType getModuleType()
	{
		return ModuleType.MODIFIER;
	}
}
