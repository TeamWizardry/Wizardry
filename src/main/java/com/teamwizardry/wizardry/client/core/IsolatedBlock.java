package com.teamwizardry.wizardry.client.core;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nonnull;

public final class IsolatedBlock implements IBlockAccess {

	public static final BlockPos POS = BlockPos.ORIGIN;

	private static final int SKY_LIGHT = 15 << 20;

	private final IBlockState state;

	private final TileEntity entity;

	public IsolatedBlock(IBlockState state, TileEntity entity) {
		this.state = state;
		this.entity = entity;
	}

	@Override
	public TileEntity getTileEntity(@Nonnull BlockPos pos) {
		return POS.equals(pos) ? entity : null;
	}

	@Override
	public int getCombinedLight(@Nonnull BlockPos pos, int lightValue) {
		return SKY_LIGHT | (POS.equals(pos) ? state.getLightValue(this, POS) : 0) << 4;
	}

	@Nonnull
	@Override
	public IBlockState getBlockState(@Nonnull BlockPos pos) {
		return POS.equals(pos) ? state : Blocks.AIR.getDefaultState();
	}

	@Override
	public boolean isAirBlock(@Nonnull BlockPos pos) {
		return !POS.equals(pos) || state.getBlock().isAir(state, this, pos);
	}

	@Nonnull
	@Override
	public Biome getBiome(@Nonnull BlockPos pos) {
		return Biomes.DEFAULT;
	}

	@Override
	public int getStrongPower(@Nonnull BlockPos pos, @Nonnull EnumFacing direction) {
		return 0;
	}

	@Nonnull
	@Override
	public WorldType getWorldType() {
		return WorldType.CUSTOMIZED;
	}

	@Override
	public boolean isSideSolid(@Nonnull BlockPos pos, @Nonnull EnumFacing side, boolean _default) {
		return POS.equals(pos) && state.isSideSolid(this, pos, side);
	}
}
