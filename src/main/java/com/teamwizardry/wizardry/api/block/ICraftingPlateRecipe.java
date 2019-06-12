package com.teamwizardry.wizardry.api.block;

import com.teamwizardry.wizardry.api.capability.player.mana.IManaCapability;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;

import java.util.function.Function;

public interface ICraftingPlateRecipe {

	boolean doesRecipeExistForItem(ItemStack stack);

	boolean doesRecipeExistInWorld(World world, BlockPos pos);

	void tick(World world, BlockPos pos, ItemStack input, ItemStackHandler inventoryHandler, Function<IManaCapability, Double> consumeMana);

	boolean isDone(World world, BlockPos pos, ItemStack input);

	void complete(World world, BlockPos pos, ItemStack input, ItemStackHandler inventoryHandler);

	void canceled(World world, BlockPos pos, ItemStack stack);

	@SideOnly(Side.CLIENT)
	void renderInput(World world, BlockPos pos, ItemStack input, float partialTicks);
}
