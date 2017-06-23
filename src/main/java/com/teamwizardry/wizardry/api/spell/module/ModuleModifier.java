package com.teamwizardry.wizardry.api.spell.module;

import com.teamwizardry.wizardry.api.spell.IModifier;

public abstract class ModuleModifier extends Module implements IModifier
{
	@Override
	public ModuleType getModuleType()
	{
		return ModuleType.MODIFIER;
	}
}
