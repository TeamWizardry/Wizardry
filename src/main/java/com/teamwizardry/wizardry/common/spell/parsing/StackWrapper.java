package com.teamwizardry.wizardry.common.spell.parsing;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class StackWrapper implements Comparable<StackWrapper>
{
	public ItemStack stack;

	public StackWrapper(ItemStack stack)
	{
		this.stack = stack;
	}

	@Override
	public int compareTo(StackWrapper wrapper)
	{
		int stack;
		int other;
		
		if (wrapper == null)
			return this.hashCode();
		if (wrapper.stack == null)
			return this.stack.hashCode();
		if (this.stack.getItem() != wrapper.stack.getItem())
		{
			return this.stack.getItem().hashCode() - wrapper.stack.getItem().hashCode();
		}
		
		int damage;
		try
		{
			damage = this.stack.getItemDamage();
			if (damage == OreDictionary.WILDCARD_VALUE) return 0;
		}
		catch (NullPointerException e)
		{
			damage = OreDictionary.WILDCARD_VALUE;
		}
		stack = OreDictionary.WILDCARD_VALUE * damage + this.stack.stackSize;
		try
		{
			damage = wrapper.stack.getItemDamage();
			if (damage == OreDictionary.WILDCARD_VALUE) return 0;
		}
		catch (NullPointerException e)
		{
			damage = OreDictionary.WILDCARD_VALUE;
		}
		other = OreDictionary.WILDCARD_VALUE * damage + wrapper.stack.stackSize;
		return stack - other;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof StackWrapper)
		{
			ItemStack item = ((StackWrapper) o).stack;
			if (item.getItem() != stack.getItem()) return false;
			if (item.getItemDamage() == OreDictionary.WILDCARD_VALUE) return true;
			if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) return true;
			return item.getItemDamage() == stack.getItemDamage();
		}
		if (o instanceof ItemStack)
		{
			ItemStack item = (ItemStack) o;
			if (item.getItem() != stack.getItem()) return false;
			if (item.getItemDamage() == OreDictionary.WILDCARD_VALUE) return true;
			if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) return true;
			return item.getItemDamage() == stack.getItemDamage();
		}
		return false;
	}
}
