package com.teamwizardry.wizardry.common.module.modifiers;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.spell.attribute.Attributes;
import com.teamwizardry.wizardry.api.spell.attribute.Operation;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import javax.annotation.Nonnull;

import javax.annotation.Nonnull;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleModifierExtendRange extends ModuleModifier {

	@Nonnull
	@Override
	public String getID() {
		return "modifier_extend_range";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Extend Range";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will increase the range or reach of the spell.";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		return true;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {

	}

	@Override
	public void apply(@Nonnull Module module) {
		module.modifiers.add(new AttributeModifier(Attributes.RANGE, 1, Operation.ADD));
		module.modifiers.add(new AttributeModifier(Attributes.MANA, 1.05, Operation.MULTIPLY));
		module.modifiers.add(new AttributeModifier(Attributes.BURNOUT, 1.05, Operation.MULTIPLY));
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleModifierExtendRange());
	}
}
