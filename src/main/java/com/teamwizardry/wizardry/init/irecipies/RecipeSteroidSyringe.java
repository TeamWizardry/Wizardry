package com.teamwizardry.wizardry.init.irecipies;

import com.teamwizardry.wizardry.common.block.fluid.ModFluids;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;

public class RecipeSteroidSyringe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	@Override
	public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn) {
		boolean foundSyringe = false;
		boolean foundMana = false;
		boolean foundNacre = false;
		boolean foundLava = false;
		boolean foundDevilDust = false;

		ItemStack mana = FluidUtil.getFilledBucket(new FluidStack(ModFluids.MANA.getActual(), 1));
		ItemStack nacre = FluidUtil.getFilledBucket(new FluidStack(ModFluids.NACRE.getActual(), 1));

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.getItem() == ModItems.SYRINGE && stack.getItemDamage() == 0) foundSyringe = true;
			if (ItemStack.areItemStacksEqual(mana, stack)) foundMana = true;
			if (ItemStack.areItemStacksEqual(nacre, stack)) foundNacre = true;
			if (stack.getItem() == Items.LAVA_BUCKET) foundLava = true;
			if (stack.getItem() == ModItems.DEVIL_DUST) foundDevilDust = true;
		}
		return foundSyringe && foundMana && foundDevilDust && foundLava && foundNacre;
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
		return new ItemStack(ModItems.SYRINGE, 1, 2);
	}

	@Override
	public boolean canFit(int width, int height) {
		return true;
	}

	@Nonnull
	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}
}
