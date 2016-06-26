package com.teamwizardry.wizardry.spells.modules.booleans;

import com.teamwizardry.wizardry.api.modules.Module;
import com.teamwizardry.wizardry.spells.modules.ModuleType;

public class ModuleOr extends Module
{
	@Override
	public ModuleType getType()
	{
		return ModuleType.BOOLEAN;
	}
}