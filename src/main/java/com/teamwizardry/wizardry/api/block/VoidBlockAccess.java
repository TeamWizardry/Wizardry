package com.teamwizardry.wizardry.api.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.NotNull;

public final class VoidBlockAccess implements IBlockAccess {

	private final IBlockState state;
	private IBlockAccess originalAccess;

	public VoidBlockAccess(IBlockState state, IBlockAccess originalAccess) {
		this.state = state;
		this.originalAccess = originalAccess;
	}

	@Override
	public TileEntity getTileEntity(@NotNull BlockPos pos) {
		return originalAccess.getTileEntity(pos);
	}

	@Override
	public int getCombinedLight(@NotNull BlockPos pos, int lightValue) {
		return originalAccess.getCombinedLight(pos, lightValue);
	}

	@NotNull
	@Override
	public IBlockState getBlockState(@NotNull BlockPos pos) {
		return originalAccess.getBlockState(pos);
	}

	@Override
	public boolean isAirBlock(@NotNull BlockPos pos) {
		return originalAccess.isAirBlock(pos);
	}

	@NotNull
	@Override
	public Biome getBiome(@NotNull BlockPos pos) {
		return originalAccess.getBiome(pos);
	}

	@Override
	public int getStrongPower(@NotNull BlockPos pos, @NotNull EnumFacing direction) {
		return 0;
	}

	@NotNull
	@Override
	public WorldType getWorldType() {
		return originalAccess.getWorldType();
	}

	@Override
	public boolean isSideSolid(@NotNull BlockPos pos, @NotNull EnumFacing side, boolean _default) {
		return originalAccess.isSideSolid(pos, side, _default);
	}
}