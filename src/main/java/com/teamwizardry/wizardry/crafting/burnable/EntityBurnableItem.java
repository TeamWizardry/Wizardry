package com.teamwizardry.wizardry.crafting.burnable;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nonnull;
import java.util.Map;

public class EntityBurnableItem extends EntityItem {
	private FireRecipe recipe;
	private boolean hasBurned;

	public EntityBurnableItem(World world) {
		super(world);
		this.isImmuneToFire = true;
	}

	public EntityBurnableItem(World world, double x, double y, double z) {
		super(world, x, y, z);
		this.isImmuneToFire = true;
	}

	public EntityBurnableItem(World world, double x, double y, double z, ItemStack stack) {
		super(world, x, y, z, stack);
		this.isImmuneToFire = true;
	}

	public static boolean isBurnable(ItemStack stack) {
		return !stack.isEmpty() &&
				(stack.getItem() != Items.REDSTONE || !Loader.isModLoaded("fluxnetworks")) &&
				FireRecipes.RECIPES.keySet().stream().anyMatch(item -> item.apply(stack));
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (recipe == null)
			return;
		if (hasBurned) {
			hasBurned = false;
			recipe.tick(world, this.getPosition());
			if (recipe.isFinished()) {
				this.setItem(recipe.finish(this));
				this.setPickupDelay(5);
				this.motionY = 0.8;
			}
		} else {
			recipe.reset();
		}
	}

	@Override
	public boolean isEntityInvulnerable(@Nonnull DamageSource source) {
		if (source.isFireDamage()) {
			hasBurned = true;
			return true;
		}
		return super.isEntityInvulnerable(source);
	}

	@Override
	public void setItem(@Nonnull ItemStack stack) {
		super.setItem(stack);
		Map.Entry<Ingredient, FireRecipe> recipeEntry =
				FireRecipes.RECIPES.entrySet().stream()
						.filter(item -> item.getKey().apply(stack) && !Ingredient.fromStacks(item.getValue().output).apply(stack))
						.findFirst().orElse(null);

		if (recipeEntry != null)
			recipe = recipeEntry.getValue();
		else
			recipe = null;
	}

	@Override
	public boolean isBurning() {
		return false;
	}
}
