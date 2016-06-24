package me.lordsaad.wizardry.spells.modules.modifiers;

import me.lordsaad.wizardry.api.modules.Module;
import me.lordsaad.wizardry.api.modules.attribute.Attribute;
import me.lordsaad.wizardry.api.modules.attribute.AttributeMap;
import me.lordsaad.wizardry.api.modules.attribute.AttributeModifier;
import me.lordsaad.wizardry.api.modules.attribute.AttributeModifier.Operation;
import me.lordsaad.wizardry.spells.modules.ModuleType;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleEnchantment extends Module implements IModifier, IRuntimeModifier
{
	@Override
	public ModuleType getType()
	{
		return ModuleType.EFFECT;
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
		map.putModifier(Attribute.COST, new AttributeModifier(Operation.MULTIPLY, 2));
		map.putModifier(Attribute.BURNOUT, new AttributeModifier(Operation.MULTIPLY, 2));
	}
}