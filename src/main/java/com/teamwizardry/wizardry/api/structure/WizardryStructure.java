package com.teamwizardry.wizardry.api.structure;

import com.teamwizardry.librarianlib.features.structure.Structure;
import com.teamwizardry.librarianlib.features.structure.TemplateBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The whole purpose of this class is to make getTemplateBlocks public
 */
public class WizardryStructure extends Structure {

	private final WizardryStructureBlockAccess wizardryAccess;

	public WizardryStructure(@Nonnull ResourceLocation loc) {
		super(loc);

		wizardryAccess = new WizardryStructureBlockAccess(blockAccess);
	}

	public WizardryStructureBlockAccess getWizardryAccess() {
		return wizardryAccess;
	}

	public static class WizardryStructureBlockAccess implements IBlockAccess {

		private final TemplateBlockAccess originalAccess;

		WizardryStructureBlockAccess(TemplateBlockAccess originalAccess) {
			this.originalAccess = originalAccess;
		}

		@Nullable
		@Override
		public TileEntity getTileEntity(@NotNull BlockPos pos) {
			return originalAccess.getTileEntity(pos);
		}

		@Override
		public int getCombinedLight(@NotNull BlockPos pos, int lightValue) {
			int sky = EnumSkyBlock.SKY.defaultLightValue;
			int block = Math.max(EnumSkyBlock.BLOCK.defaultLightValue, lightValue);
			return sky << 20 | block << 4;
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
			return originalAccess.getStrongPower(pos, direction);
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
}
