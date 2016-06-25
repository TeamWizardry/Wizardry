package me.lordsaad.wizardry.spells.modules.modifiers;

import net.minecraft.nbt.NBTTagCompound;
import me.lordsaad.wizardry.api.modules.Module;
import me.lordsaad.wizardry.api.modules.attribute.Attribute;
import me.lordsaad.wizardry.api.modules.attribute.AttributeMap;
import me.lordsaad.wizardry.api.modules.attribute.AttributeModifier;
import me.lordsaad.wizardry.api.modules.attribute.AttributeModifier.Operation;
import me.lordsaad.wizardry.api.modules.attribute.AttributeModifier.Priority;
import me.lordsaad.wizardry.spells.modules.ModuleType;

public class ModuleSticky extends Module implements IModifier, IRuntimeModifier
{
	@Override
	public ModuleType getType()
	{
		return ModuleType.MODIFIER;
	}
	
	@Override
	public NBTTagCompound saveToNBT()
	{
		return null;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		
	}
	
	@Override
	public void apply(AttributeMap map)
	{
		map.putModifier(Attribute.COST, new AttributeModifier(Operation.MULTIPLY, 1.2));
		map.putModifier(Attribute.BURNOUT, new AttributeModifier(Operation.MULTIPLY, 1.2));
		map.putModifier(Attribute.COST, new AttributeModifier(Operation.ADD, 1, Priority.LOW));
		map.putModifier(Attribute.BURNOUT, new AttributeModifier(Operation.ADD, 1, Priority.LOW));
	}
}