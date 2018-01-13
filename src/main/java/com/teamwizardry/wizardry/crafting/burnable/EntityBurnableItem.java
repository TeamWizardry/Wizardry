package com.teamwizardry.wizardry.crafting.burnable;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.OreDictionary;

public class EntityBurnableItem extends EntityItem
{
	private FireRecipe recipe;
	private boolean hasBurned;
	
	public EntityBurnableItem(World world)
	{
		super(world);
		this.isImmuneToFire = true;
	}
	
	public EntityBurnableItem(World world, double x, double y, double z)
	{
		super(world, x, y, z);
		this.isImmuneToFire = true;
	}
	
	public EntityBurnableItem(World world, double x, double y, double z, ItemStack stack)
	{
		super(world, x, y, z, stack);
		this.isImmuneToFire = true;
	}
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		if (recipe == null)
			return;
		if (hasBurned)
		{
			hasBurned = false;
			recipe.tick(world, this.getPosition());
			if (recipe.isFinished())
			{
				this.setItem(recipe.finish(this));
				this.setPickupDelay(5);
				this.motionY = 0.8;
			}
		}
		else
		{
			recipe.reset();
		}
	}

	@Override
	public boolean isEntityInvulnerable(DamageSource source)
	{
		if (source.isFireDamage())
		{
			hasBurned = true;
			return true;
		}
		return super.isEntityInvulnerable(source);
	}
	
	@Override
	public void setItem(ItemStack stack)
	{
		super.setItem(stack);
		ItemStack key = FireRecipes.ITEM_RECIPES.keySet().stream().filter(item -> ItemStack.areItemsEqual(item, stack)).findFirst().orElse(null);
		if (FireRecipes.ITEM_RECIPES.containsKey(key))
		{
			recipe = FireRecipes.ITEM_RECIPES.get(key).copy();
			return;
		}
		else
		{
			int[] oreIds = OreDictionary.getOreIDs(stack);
			for (int oreId : oreIds)
			{
				if (FireRecipes.OREDICT_RECIPES.containsKey(OreDictionary.getOreName(oreId)))
				{
					recipe = FireRecipes.OREDICT_RECIPES.get(OreDictionary.getOreName(oreId));
					return;
				}
			}
		}
		recipe = null;
	}
	
	@Override
	public boolean isBurning()
	{
		return false;
	}
	
	public static boolean isBurnable(ItemStack stack)
	{
		if (stack.isEmpty())
			return false;
		if (stack.getItem() == Items.REDSTONE && Loader.isModLoaded("fluxnetworks"))
			return false;
		if (FireRecipes.ITEM_RECIPES.keySet().stream().anyMatch(item -> ItemStack.areItemsEqual(item, stack)))
			return true;
		int[] oreIds = OreDictionary.getOreIDs(stack);
		for (int oreId : oreIds)
			if (FireRecipes.OREDICT_RECIPES.containsKey(OreDictionary.getOreName(oreId)))
				return true;
		return false;
	}
}
