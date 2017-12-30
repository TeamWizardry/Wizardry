package com.teamwizardry.wizardry.crafting.burnable;

import java.io.File;
import java.util.HashMap;

import com.teamwizardry.wizardry.init.ModItems;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class FireRecipes
{
	public static final FireRecipes INSTANCE = new FireRecipes();
	
	public static final HashMap<ItemStack, FireRecipe> RECIPES = new HashMap<>();
	
	public void loadRecipes(File directory)
	{
		RECIPES.put(new ItemStack(Items.REDSTONE), new FireRecipe(new ItemStack(ModItems.DEVIL_DUST), 200));
		RECIPES.put(new ItemStack(ModItems.FAIRY_DUST), new FireRecipe(new ItemStack(ModItems.SKY_DUST), 200));
	}
}
