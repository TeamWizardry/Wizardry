package com.teamwizardry.wizardry.common.block.fluid.nacre;

import com.teamwizardry.wizardry.common.init.ModBlocks;
import com.teamwizardry.wizardry.common.init.ModFluids;
import com.teamwizardry.wizardry.common.init.ModItems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class NacreFluid extends FlowableFluid
{
    @Override public Fluid getFlowing() { return ModFluids.FLOWING_NACRE; }

    @Override public Fluid getStill() { return ModFluids.STILL_NACRE; }

    @Override protected boolean isInfinite() { return false; }

    @Override
    protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state)
    {
        BlockEntity entity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
        Block.dropStacks(state, world, pos, entity);
    }

    @Override protected int getFlowSpeed(WorldView world) { return 4; }

    @Override protected int getLevelDecreasePerBlock(WorldView world) { return 1; }

    @Override public Item getBucketItem() { return ModItems.nacreBucket; }

    @Override protected boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) { return false; }

    @Override public int getTickRate(WorldView world) { return 200; }

    @Override protected float getBlastResistance() { return 100.0F; }

    @Override protected BlockState toBlockState(FluidState state) { return ModBlocks.liquidNacre.getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(state)); }
    
    public static class Flowing extends NacreFluid
    {
        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder)
        {
            super.appendProperties(builder);
            builder.add(LEVEL);
        }

        @Override public int getLevel(FluidState state) { return state.get(LEVEL); }

        @Override public boolean isStill(FluidState state) { return false; }
    }
    
    public static class Still extends NacreFluid
    {
        @Override public int getLevel(FluidState state) { return 8; }
        
        @Override public boolean isStill(FluidState state) { return true; }
    }
}
