package com.teamwizardry.wizardry.common.spell.module.modifiers;

import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.module.attribute.AttributeMap;
import com.teamwizardry.wizardry.api.module.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.spell.IModifier;
import com.teamwizardry.wizardry.api.spell.ModuleType;

public class ModuleScatter extends Module implements IModifier
{
	public ModuleScatter()
	{
		canHaveChildren = false;
	}

	@Override
	public ModuleType getType()
	{
		return ModuleType.MODIFIER;
	}

	@Override
	public String getDescription()
	{
		return "Gives a spell a scatter effect. For single projectiles, the spell aims differently each cast. For multiple projectiles, the spread of projectiles increases.";
	}

	@Override
	public void apply(AttributeMap map)
	{
		map.putModifier(Attribute.SCATTER, new AttributeModifier(AttributeModifier.Operation.ADD, 0.1));

		map.putModifier(Attribute.MANA, new AttributeModifier(AttributeModifier.Operation.MULTIPLY, 1));
		map.putModifier(Attribute.BURNOUT, new AttributeModifier(AttributeModifier.Operation.MULTIPLY, 1));
	}
}