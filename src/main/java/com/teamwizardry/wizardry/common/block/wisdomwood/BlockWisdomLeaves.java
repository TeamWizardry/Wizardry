package com.teamwizardry.wizardry.common.block.wisdomwood;

import com.teamwizardry.librarianlib.features.base.block.BlockModLeaves;
import com.teamwizardry.librarianlib.features.base.block.IBlockColorProvider;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function4;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeColorHelper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockWisdomLeaves extends BlockModLeaves implements IBlockColorProvider {

	public BlockWisdomLeaves() {
		super("wisdom_leaves");
	}

	@Nullable
	@Override
	public Item getItemDropped(@Nonnull IBlockState state, @Nonnull Random rand, int fortune) {
		return null;
	}

	@Nullable
	@Override
	public Function2<ItemStack, Integer, Integer> getItemColorFunction() {
		return (stack, integer) -> ColorizerFoliage.getFoliageColorBasic();
	}

	@Nullable
	@Override
	public Function4<IBlockState, IBlockAccess, BlockPos, Integer, Integer> getBlockColorFunction() {
		return (iBlockState, iBlockAccess, blockPos, integer) -> iBlockAccess != null && blockPos != null ? BiomeColorHelper.getFoliageColorAtPos(iBlockAccess, blockPos) : ColorizerFoliage.getFoliageColorBasic();
	}
}
