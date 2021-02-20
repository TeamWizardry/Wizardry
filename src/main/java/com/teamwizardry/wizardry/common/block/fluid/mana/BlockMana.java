package com.teamwizardry.wizardry.common.block.fluid.mana;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockMana extends FlowingFluidBlock {
    public BlockMana(Supplier<? extends FlowingFluid> supplier,
                     Properties properties) {
        super(supplier, properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        // TODO float

        super.onEntityCollision(state, worldIn, pos, entityIn);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {

        // TODO: particles
        super.animateTick(stateIn, worldIn, pos, rand);
    }
}
