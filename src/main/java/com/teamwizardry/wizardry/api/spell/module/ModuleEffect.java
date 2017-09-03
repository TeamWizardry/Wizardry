package com.teamwizardry.wizardry.api.spell.module;

import com.teamwizardry.wizardry.api.spell.ITaxing;
import javax.annotation.Nonnull;

public abstract class ModuleEffect extends Module implements ITaxing
{
	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}
}
