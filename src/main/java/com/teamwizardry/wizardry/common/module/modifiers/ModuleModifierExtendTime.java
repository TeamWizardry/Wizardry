package com.teamwizardry.wizardry.common.module.modifiers;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.spell.attribute.Attributes;
import com.teamwizardry.wizardry.api.spell.attribute.Operation;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleModifierExtendTime extends ModuleModifier {

	@Nonnull
	@Override
	public String getID() {
		return "modifier_extend_time";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Extend Time";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will increase the duration of the spell.";
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
		module.modifiers.add(new AttributeModifier(Attributes.DURATION, 1, Operation.ADD));
		module.modifiers.add(new AttributeModifier(Attributes.MANA, 1.2, Operation.MULTIPLY));
		module.modifiers.add(new AttributeModifier(Attributes.BURNOUT, 1.2, Operation.MULTIPLY));
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleModifierExtendTime());
	}
}
