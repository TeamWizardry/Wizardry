package me.lordsaad.wizardry.spells.modules.modifiers;

import me.lordsaad.wizardry.api.modules.Module;
import me.lordsaad.wizardry.api.modules.attribute.Attribute;
import me.lordsaad.wizardry.api.modules.attribute.AttributeMap;
import me.lordsaad.wizardry.api.modules.attribute.AttributeModifier;
import me.lordsaad.wizardry.api.modules.attribute.AttributeModifier.Operation;
import me.lordsaad.wizardry.spells.modules.ModuleType;

public class ModuleMeleeDamage extends Module implements IModifier
{
	public ModuleMeleeDamage()
	{
		canHaveChildren = false;
	}
	
	@Override
	public ModuleType getType()
	{
		return ModuleType.MODIFIER;
	}

	@Override
	public void apply(AttributeMap map)
	{
		map.putModifier(Attribute.DAMAGE, new AttributeModifier(Operation.ADD, 1));
		
		map.putModifier(Attribute.COST, new AttributeModifier(Operation.MULTIPLY, 1.2));
		map.putModifier(Attribute.BURNOUT, new AttributeModifier(Operation.MULTIPLY, 1.2));
	}
}