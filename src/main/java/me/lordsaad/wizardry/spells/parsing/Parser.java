package me.lordsaad.wizardry.spells.parsing;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import me.lordsaad.wizardry.ModItems;
import me.lordsaad.wizardry.api.modules.Module;
import me.lordsaad.wizardry.api.modules.ModuleList;
import net.minecraft.item.ItemStack;

public class Parser {

	private Deque<Module> moduleStack = new ArrayDeque<>();
	private Deque<ItemStack> stacks;
	private int endCount = 0;
	
	
	public Parser(List<ItemStack> items) {
		stacks = new ArrayDeque<>(items);
	}
	
	private Module parseSub() {
		Module module = getModuleForItem(stacks.pop());
		if(module == null)
			return module;
		if(module.canHaveChildren()) {
			while(endCount == 0) {
				endCount = getEndCount(stacks.peek());
				if(endCount != 0) {
					stacks.pop();
					break;
				}
				
				Module childModule = parseSub();
				if(childModule != null)
					module.accept(childModule);
			}
			endCount--;
		}
		
		return module;
	}
	
	public Module parse() {
		return parseSub();
	}
	
	{ /* boilerplate */ }
	
	/**
	 * Gets a new instance of the module given an item
	 */
	private Module getModuleForItem(ItemStack stack) {
		return ModuleList.INSTANCE.createModule(stack);
	}
	
	/**
	 * Gets the number of levels to end given an item. 0 if the item isn't an end item
	 */
	private int getEndCount(ItemStack stack) {
		return stack.getItem() == ModItems.vinteumDust ? 1 : 0;
	}
	
}
