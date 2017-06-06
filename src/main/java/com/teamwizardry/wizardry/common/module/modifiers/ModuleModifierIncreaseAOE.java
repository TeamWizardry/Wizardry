package com.teamwizardry.wizardry.common.module.modifiers;

import com.teamwizardry.wizardry.api.spell.*;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleModifierIncreaseAOE extends Module implements IModifier {

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.MODIFIER;
	}

	@Nonnull
	@Override
	public String getID() {
		return "modifier_aoe";
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

	@Override
	public void apply(@NotNull Module module) {
		int power = 1;
		module.attributes.setDouble(Attributes.INCREASE_AOE, module.attributes.getDouble(Attributes.INCREASE_AOE) + power);
	}

	@Override
	public double costMultiplier() {
		return 1.2;
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleModifierIncreaseAOE());
	}
}
