package com.teamwizardry.wizardry.common.world;

import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * Created by Demoniaque.
 */
public class WorldGeneratorWisdomTree extends WorldGenAbstractTree {

	private static final IBlockState LOG = ModBlocks.WISDOM_WOOD_LOG.getDefaultState();
	private static final IBlockState LEAF = ModBlocks.WISDOM_LEAVES.getDefaultState();

	public WorldGeneratorWisdomTree(boolean notify) {
		super(notify);
	}

	@Override
	public boolean generate(@Nonnull World worldIn, @Nonnull Random rand, @Nonnull BlockPos position) {
		int height = rand.nextInt(5) + 5;

		boolean canFit = true;

		if (position.getY() >= 1 && position.getY() + height + 1 <= 256) {
			for (int currentY = position.getY(); currentY <= position.getY() + 1 + height; ++currentY) {
				int leafWidth = 0;

				if (currentY >= position.getY() + (height / 2))
					leafWidth = 2;

				if (currentY >= position.getY() + height - 2) {
					leafWidth = 1;
				}

				BlockPos.MutableBlockPos poses = new BlockPos.MutableBlockPos();

				for (int searchX = position.getX() - leafWidth; searchX <= position.getX() + leafWidth && canFit; ++searchX) {
					for (int searchZ = position.getZ() - leafWidth; searchZ <= position.getZ() + leafWidth && canFit; ++searchZ) {
						if (currentY >= 0 && currentY < worldIn.getHeight()) {
							if (!this.isReplaceable(worldIn, poses.setPos(searchX, currentY, searchZ))) {
								canFit = false;
							}
						} else {
							canFit = false;
						}
					}
				}
			}

			if (!canFit) {
				return false;
			} else {
				BlockPos down = position.down();
				IBlockState state = worldIn.getBlockState(down);
				boolean isSoil = state.getBlock().canSustainPlant(state, worldIn, down, net.minecraft.util.EnumFacing.UP, (net.minecraft.block.BlockSapling) Blocks.SAPLING);

				if (isSoil && position.getY() < worldIn.getHeight() - height - 1) {
					state.getBlock().onPlantGrow(state, worldIn, down, position);

					for (int i2 = position.getY() - 3 + height; i2 <= position.getY() + height; ++i2) {
						int k2 = i2 - (position.getY() + height);
						int l2 = 1 - k2 / 2;

						for (int i3 = position.getX() - l2; i3 <= position.getX() + l2; ++i3) {
							int j1 = i3 - position.getX();

							for (int k1 = position.getZ() - l2; k1 <= position.getZ() + l2; ++k1) {
								int l1 = k1 - position.getZ();

								if (Math.abs(j1) != l2 || Math.abs(l1) != l2 || rand.nextInt(2) != 0 && k2 != 0) {
									BlockPos blockpos = new BlockPos(i3, i2, k1);
									IBlockState state2 = worldIn.getBlockState(blockpos);

									if (state2.getBlock().isAir(state2, worldIn, blockpos) || state2.getBlock().isAir(state2, worldIn, blockpos)) {
										this.setBlockAndNotifyAdequately(worldIn, blockpos, LEAF);
									}
								}
							}
						}
					}

					for (int j2 = 0; j2 < height; ++j2) {
						BlockPos upN = position.up(j2);
						IBlockState state2 = worldIn.getBlockState(upN);

						if (state2.getBlock().isAir(state2, worldIn, upN) || state2.getBlock().isLeaves(state2, worldIn, upN)) {
							this.setBlockAndNotifyAdequately(worldIn, position.up(j2), LOG);
						}
					}

					return true;
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
	}
}
