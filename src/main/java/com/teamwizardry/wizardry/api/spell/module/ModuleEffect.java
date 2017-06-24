package com.teamwizardry.wizardry.api.spell.module;

import com.teamwizardry.wizardry.api.spell.ITaxing;
import org.jetbrains.annotations.NotNull;

public abstract class ModuleEffect extends Module implements ITaxing
{
	@NotNull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}
}
