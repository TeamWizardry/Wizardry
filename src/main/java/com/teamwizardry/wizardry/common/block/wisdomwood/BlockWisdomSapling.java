package com.teamwizardry.wizardry.common.block.wisdomwood;

import com.teamwizardry.librarianlib.features.base.block.BlockModSapling;
import com.teamwizardry.wizardry.init.ModBlocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class BlockWisdomSapling extends BlockModSapling {

	public BlockWisdomSapling() {
		super("wisdom_sapling");
	}

	@Override
	public void generateTree(@NotNull World world, @NotNull BlockPos pos, @NotNull IBlockState state, @NotNull Random rand) {
		Companion.defaultSaplingBehavior(world, pos, state, rand, ModBlocks.WISDOM_WOOD_LOG, ModBlocks.WISDOM_LEAVES);
	}

	@NotNull
	@Override
	public EnumRarity getBlockRarity(@NotNull ItemStack stack) {
		return EnumRarity.UNCOMMON;
	}
}
