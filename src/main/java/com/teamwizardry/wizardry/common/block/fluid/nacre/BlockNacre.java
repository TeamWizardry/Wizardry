package com.teamwizardry.wizardry.common.block.fluid.nacre;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockNacre extends FluidBlock {
    public BlockNacre(FlowableFluid fluid,
                     Settings settings) {
        super(fluid, settings);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        // TODO float
        super.onEntityCollision(state, world, pos, entity);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random rand) {
        // TODO: particles
        super.randomDisplayTick(state, world, pos, rand);
    }
}
