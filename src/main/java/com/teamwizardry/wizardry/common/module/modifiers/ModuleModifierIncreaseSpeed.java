package com.teamwizardry.wizardry.common.module.modifiers;

import com.teamwizardry.wizardry.api.spell.module.IModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque.
 */
@RegisterModule
public class ModuleModifierIncreaseSpeed implements IModuleModifier {

	@Nonnull
	@Override
	public String getClassID() {
		return "modifier_increase_speed";
	}
}
