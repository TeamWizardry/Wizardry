package com.teamwizardry.wizardry.common.core.craftingplaterecipes;

import com.teamwizardry.wizardry.api.block.ICraftingPlateRecipe;
import com.teamwizardry.wizardry.api.capability.mana.CapManager;
import com.teamwizardry.wizardry.api.capability.mana.IWizardryCapability;
import com.teamwizardry.wizardry.common.tile.TileJar;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;

import java.util.function.Function;

public class FairyJarRecipe implements ICraftingPlateRecipe {

	@Override
	public boolean doesRecipeExistForItem(ItemStack stack) {
		return false;
	}

	@Override
	public boolean doesRecipeExistInWorld(World world, BlockPos pos) {
		TileEntity tileEntity = world.getTileEntity(pos.offset(EnumFacing.UP));
		return tileEntity instanceof TileJar && ((TileJar) tileEntity).hasFairy;
	}

	@Override
	public void tick(World world, BlockPos pos, ItemStack input, ItemStackHandler inventoryHandler, Function<IWizardryCapability, Double> consumeMana) {
		TileEntity tileEntity = world.getTileEntity(pos.offset(EnumFacing.UP));
		if (!(tileEntity instanceof TileJar)) return;
		TileJar jar = (TileJar) tileEntity;

		if (jar.hasFairy && !CapManager.isManaFull(jar.cap.getHandler())) {
			CapManager.forObject(jar.cap.getHandler()).addMana(consumeMana.apply(jar.cap.getHandler())).close();
		}
	}

	@Override
	public void complete(World world, BlockPos pos, ItemStack input, ItemStackHandler inventoryHandler) {

	}

	@Override
	public boolean isDone(World world, BlockPos pos, ItemStack stack) {
		TileEntity tileEntity = world.getTileEntity(pos.offset(EnumFacing.UP));
		if (!(tileEntity instanceof TileJar)) return false;
		TileJar jar = (TileJar) tileEntity;

		return CapManager.isManaFull(jar.cap.getHandler());
	}

	@Override
	public void canceled(World world, BlockPos pos, ItemStack stack) {
		TileEntity tileEntity = world.getTileEntity(pos.offset(EnumFacing.UP));
		if (!(tileEntity instanceof TileJar)) return;
		TileJar jar = (TileJar) tileEntity;

		if (!CapManager.isManaFull(jar.cap.getHandler())) {
			CapManager.forObject(jar.cap.getHandler()).setMana(0).close();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderInput(World world, BlockPos pos, ItemStack input, float partialTicks) {

	}

}
