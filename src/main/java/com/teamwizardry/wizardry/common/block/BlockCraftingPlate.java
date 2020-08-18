package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.wizardry.client.gui.WorktableGUI;
import com.teamwizardry.wizardry.common.lib.LibTileEntityType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockCraftingPlate extends Block {

	public BlockCraftingPlate(Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull ActionResultType onBlockActivated(@NotNull BlockState state, @NotNull World worldIn, @NotNull BlockPos pos, @NotNull PlayerEntity player, @NotNull Hand handIn, @NotNull BlockRayTraceResult hit) {

		Minecraft.getInstance().displayGuiScreen(new WorktableGUI());

		return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return LibTileEntityType.CRAFTING_PLATE.create();
	}
}
