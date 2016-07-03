package com.teamwizardry.wizardry.common.spell.parsing;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import net.minecraft.item.ItemStack;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.init.ModItems;

public class Parser
{
	private Deque<ItemStack> stacks;
	private int endCount = 0;
	
	{ /* boilerplate */}

	public Parser(List<ItemStack> items)
	{
		stacks = new ArrayDeque<>(items);
	}

	private Module parseSub(ModuleType currentType, ModuleType expectedType)
	{
		Module module = getModuleForItem(stacks.pop(), expectedType);
		if (module == null)
			return module;
		currentType = module.getType();
		expectedType = getDefaultType(currentType);
		if (module.canHaveChildren())
		{
			while (endCount == 0 && expectedType != null)
			{
				if (stacks.isEmpty()) return module;
				endCount = getEndCount(stacks.peek());
				if (endCount != 0)
				{
					while (endCount != 0 && expectedType != null)
					{
						expectedType = getNextType(currentType, expectedType);
						endCount--;
					}
					if (endCount != 0) 
					{
						stacks.pop();
						break;
					}
				}

				Module childModule = parseSub(currentType, expectedType);
				if (childModule != null)
					module.accept(childModule);				
			}
			endCount--;
		}
		return module;
	}

	public Module parse()
	{
		return parseSub(ModuleType.EVENT, getDefaultType(ModuleType.EVENT));
	}

	/**
	 * Gets a new instance of the module given an item
	 */
	private Module getModuleForItem(ItemStack stack, ModuleType type)
	{
		return Wizardry.moduleList.createModule(stack, type);
	}

	/**
	 * Gets the number of levels to end given an item. 0 if the item isn't an
	 * end item
	 */
	private int getEndCount(ItemStack stack)
	{
		if (stack == null)
			return 0;
		return stack.getItem() == ModItems.VINTEUM_DUST ? 1 : 0;
	}

	private ModuleType getNextType(ModuleType currentType, ModuleType expectedType)
	{
		switch (currentType)
		{
			case MODIFIER:
			case EFFECT:
			case EVENT:
				return null;
			case SHAPE:
				if (expectedType == ModuleType.MODIFIER)
					return ModuleType.BOOLEAN;
			case BOOLEAN:
				if (expectedType == ModuleType.BOOLEAN)
					return ModuleType.EVENT;
				if (expectedType == ModuleType.EVENT)
					return ModuleType.EFFECT;
				if (expectedType == ModuleType.EFFECT)
					return ModuleType.SHAPE;
				if (expectedType == ModuleType.SHAPE)
					return null;
			default:
				return null;
		}
	}
	
	private ModuleType getDefaultType(ModuleType currentType)
	{
		switch (currentType)
		{
			case MODIFIER:
				return ModuleType.MODIFIER;
			case EFFECT:
				return ModuleType.MODIFIER;
			case EVENT:
				return ModuleType.SHAPE;
			case SHAPE:
				return ModuleType.MODIFIER;
			case BOOLEAN:
				return ModuleType.BOOLEAN;
			default:
				return null;
		}
	}
}
