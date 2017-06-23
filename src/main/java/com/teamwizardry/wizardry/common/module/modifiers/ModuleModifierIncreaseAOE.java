package com.teamwizardry.wizardry.common.module.modifiers;

import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.spell.attribute.Attributes;
import com.teamwizardry.wizardry.api.spell.attribute.Operation;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

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

	@Override
	public void apply(@NotNull Module module) {
		module.modifiers.add(new AttributeModifier(Attributes.AREA, 1, Operation.ADD));
		module.modifiers.add(new AttributeModifier(Attributes.MANA, 1.2, Operation.MULTIPLY));
		module.modifiers.add(new AttributeModifier(Attributes.BURNOUT, 1.2, Operation.MULTIPLY));
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleModifierIncreaseAOE());
	}
}
