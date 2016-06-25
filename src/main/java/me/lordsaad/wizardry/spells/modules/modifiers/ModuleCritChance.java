package me.lordsaad.wizardry.spells.modules.modifiers;

import me.lordsaad.wizardry.api.modules.Module;
import me.lordsaad.wizardry.api.modules.attribute.Attribute;
import me.lordsaad.wizardry.api.modules.attribute.AttributeMap;
import me.lordsaad.wizardry.api.modules.attribute.AttributeModifier;
import me.lordsaad.wizardry.api.modules.attribute.AttributeModifier.Operation;
import me.lordsaad.wizardry.api.modules.attribute.AttributeModifier.Priority;
import me.lordsaad.wizardry.spells.modules.ModuleType;

public class ModuleCritChance extends Module implements IModifier
{
	public ModuleCritChance()
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
		map.putModifier(Attribute.CRIT_CHANCE, new AttributeModifier(Operation.ADD, 0.1));
		
		// 100% Crit Chance ~ 
		map.putModifier(Attribute.COST, new AttributeModifier(Operation.MULTIPLY, 1.5, Priority.LOW));
		map.putModifier(Attribute.BURNOUT, new AttributeModifier(Operation.MULTIPLY, 1.5, Priority.LOW));
	}
}