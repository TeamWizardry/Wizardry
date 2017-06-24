package com.teamwizardry.wizardry.api.spell.module;

import com.teamwizardry.wizardry.api.spell.ICostModifier;
import org.jetbrains.annotations.NotNull;

public abstract class ModuleShape extends Module implements ICostModifier
{
	@NotNull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.SHAPE;
	}
}
