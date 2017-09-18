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
public class ModuleModifierIncreaseSpeed extends ModuleModifier {

	@Nonnull
	@Override
	public String getID() {
		return "modifier_increase_speed";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Increase Speed";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will increase the speed of the spell, like the speed of a projectile";
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
		return cloneModule(new ModuleModifierIncreaseSpeed());
	}
}
