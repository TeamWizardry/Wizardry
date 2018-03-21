package com.teamwizardry.wizardry.api.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nonnull;

public final class VoidBlockAccess implements IBlockAccess {

	private final IBlockState state;
	private IBlockAccess originalAccess;

	public VoidBlockAccess(IBlockState state, IBlockAccess originalAccess) {
		this.state = state;
		this.originalAccess = originalAccess;
	}

	@Override
	public TileEntity getTileEntity(@Nonnull BlockPos pos) {
		return originalAccess.getTileEntity(pos);
	}

	@Override
	public int getCombinedLight(@Nonnull BlockPos pos, int lightValue) {
		return originalAccess.getCombinedLight(pos, lightValue);
	}

	@Nonnull
	@Override
	public IBlockState getBlockState(@Nonnull BlockPos pos) {
		return originalAccess.getBlockState(pos);
	}

	@Override
	public boolean isAirBlock(@Nonnull BlockPos pos) {
		return originalAccess.isAirBlock(pos);
	}

	@Nonnull
	@Override
	public Biome getBiome(@Nonnull BlockPos pos) {
		return originalAccess.getBiome(pos);
	}

	@Override
	public int getStrongPower(@Nonnull BlockPos pos, @Nonnull EnumFacing direction) {
		return 0;
	}

	@Nonnull
	@Override
	public WorldType getWorldType() {
		return originalAccess.getWorldType();
	}

	@Override
	public boolean isSideSolid(@Nonnull BlockPos pos, @Nonnull EnumFacing side, boolean _default) {
		return originalAccess.isSideSolid(pos, side, _default);
	}
}
