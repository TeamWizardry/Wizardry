package com.teamwizardry.wizardry.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nullable;

public class IngredientFluidStack extends Ingredient {
	private final FluidStack fluid;

	ItemStack[] cachedStacks;

	public IngredientFluidStack(FluidStack fluid) {
		super(0);
		this.fluid = fluid;
	}

	public IngredientFluidStack(Fluid fluid, int amount) {
		this(new FluidStack(fluid, amount));
	}

	public FluidStack getFluid() {
		return fluid;
	}

	@Override
	public ItemStack[] getMatchingStacks() {
		if (cachedStacks == null) {
			cachedStacks = new ItemStack[]{FluidUtil.getFilledBucket(fluid)};
		}
		return this.cachedStacks;
	}

	@Override
	public boolean apply(@Nullable ItemStack stack) {
		if (stack == null) {
			return false;
		} else {
			FluidStack fluidStack = FluidUtil.getFluidContained(stack);
			return fluidStack == null && this.fluid == null || fluidStack != null && fluidStack.containsFluid(fluid);
		}
	}
}
