package com.teamwizardry.wizardry.common.spell.parsing;

import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.ModuleList;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.item.ItemStack;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class Parser
{

	private Deque<ItemStack> stacks;
	private int endCount = 0;

	{ /* boilerplate */}

	public Parser(List<ItemStack> items)
	{
		stacks = new ArrayDeque<>(items);
	}

	private Module parseSub()
	{
		Module module = getModuleForItem(stacks.pop());
		if (module == null)
			return module;
		if (module.canHaveChildren())
		{
			while (endCount == 0)
			{
				if (stacks.isEmpty()) return module;
				endCount = getEndCount(stacks.peek());
				if (endCount != 0)
				{
					stacks.pop();
					break;
				}

				Module childModule = parseSub();
				if (childModule != null)
					module.accept(childModule);
			}
			endCount--;
		}
		return module;
	}

	public Module parse()
	{
		return parseSub();
	}

	/**
	 * Gets a new instance of the module given an item
	 */
	private Module getModuleForItem(ItemStack stack)
	{
		return ModuleList.INSTANCE.createModule(stack);
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

}
