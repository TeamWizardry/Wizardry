package com.teamwizardry.wizardry.spells.modules.modifiers;

import com.teamwizardry.wizardry.api.modules.Module;
import com.teamwizardry.wizardry.api.modules.attribute.Attribute;
import com.teamwizardry.wizardry.api.modules.attribute.AttributeMap;
import com.teamwizardry.wizardry.api.modules.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.modules.attribute.AttributeModifier.Operation;
import com.teamwizardry.wizardry.spells.modules.ModuleType;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleEnchantment extends Module implements IModifier, IRuntimeModifier
{
	public ModuleEnchantment()
	{
		canHaveChildren = false;
	}
	
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