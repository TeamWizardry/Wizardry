package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.common.tile.TileUnderworldPortal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Project: Wizardry
 * Created by Carbon
 * Copyright (c) Carbon 2020
 */
public class BlockUnderworldTeleporter extends BlockModContainer {
//	private static final AxisAlignedBB AABB_PORTAL = new AxisAlignedBB(0.3, 0.3, 0.3, 0.7, 0.7, 0.7);

	public static final String name = "underworld_portal";

	public BlockUnderworldTeleporter() {
		super(name, Material.PORTAL);
		setBlockUnbreakable();
		setLightLevel(1.0F);
	}

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		if(entityIn != null && !worldIn.isRemote && !entityIn.isRiding() && !entityIn.isBeingRidden() && entityIn.isNonBoss() && entityIn.getEntityBoundingBox().intersects(state.getBoundingBox(worldIn, pos).offset(pos))) {
			entityIn.changeDimension(ConfigValues.underworldID);
		}
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(@NotNull World world, @NotNull IBlockState state) {
		return new TileUnderworldPortal();
	}
}
