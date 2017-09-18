package com.teamwizardry.wizardry.common.module.modifiers;

import javax.annotation.Nonnull;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleModifierIncreaseAOE extends ModuleModifier {

	@Nonnull
	@Override
	public String getID() {
		return "modifier_increase_aoe";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Extend Area Of Effect";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Can increase/widen area of effect spells.";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		return true;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleModifierIncreaseAOE());
	}
}
