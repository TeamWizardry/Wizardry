package com.teamwizardry.wizardry.common.module.modifiers;

import com.teamwizardry.wizardry.api.spell.*;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleModifierIncreasePotency extends Module implements IModifier {

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.MODIFIER;
	}

	@Nonnull
	@Override
	public String getID() {
		return "modifier_extend_potency";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Increase Potency";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will increase the strength of the spell.";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		return true;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {

	}

	@Override
	public void apply(@NotNull Module module) {
		int power = 1;
		module.attributes.setDouble(Attributes.INCREASE_POTENCY, module.attributes.getDouble(Attributes.INCREASE_POTENCY) + power);
	}

	@Override
	public double costMultiplier() {
		return 1.2;
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleModifierIncreasePotency());
	}
}
