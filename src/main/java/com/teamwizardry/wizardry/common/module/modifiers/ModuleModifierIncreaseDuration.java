package com.teamwizardry.wizardry.common.module.modifiers;

import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque.
 */
@RegisterModule
public class ModuleModifierIncreaseDuration extends ModuleModifier {

	@Nonnull
	@Override
	public String getID() {
		return "modifier_extend_time";
	}

}
