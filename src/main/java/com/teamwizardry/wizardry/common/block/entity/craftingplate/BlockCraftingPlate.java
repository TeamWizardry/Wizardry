package com.teamwizardry.wizardry.common.block.entity.craftingplate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.teamwizardry.wizardry.common.block.IManaNode;
import com.teamwizardry.wizardry.common.init.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class BlockCraftingPlate extends BlockWithEntity implements Waterloggable, IManaNode
{
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private static final VoxelShape SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 13.0D, 16.0D);

    public BlockCraftingPlate(Settings settings)
    {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(WATERLOGGED, Boolean.FALSE));
    }

    @Override public BlockRenderType getRenderType(BlockState state) { return BlockRenderType.MODEL; }
    
    @Override public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) { return SHAPE; }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) return checkType(type, ModBlocks.craftingPlateEntity, (w,p,s,e) -> BlockCraftingPlateEntity.clientTick(w,e));
        else return checkType(type, ModBlocks.craftingPlateEntity, (w,p,s,e) -> BlockCraftingPlateEntity.serverTick(world, e));
    }
    
    @Override
    public @NotNull ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (!world.isClient)
        {
            if (hand == Hand.MAIN_HAND)
            {
                ItemStack heldItem = player.getStackInHand(hand);
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof BlockCraftingPlateEntity)
                {
                    BlockCraftingPlateEntity plate = (BlockCraftingPlateEntity) blockEntity;
                    if (heldItem.isEmpty())
                        player.setStackInHand(hand, plate.removeItem());
                    else
                        plate.addItem(heldItem.split(1));
                }
            }
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
    {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof BlockCraftingPlateEntity)
            ((BlockCraftingPlateEntity) blockEntity).onEntityCollision(entity);
    }

    @Nullable @Override public BlockCraftingPlateEntity createBlockEntity(BlockPos pos, BlockState state) { return new BlockCraftingPlateEntity(pos, state); }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof BlockCraftingPlateEntity)
            ItemScatterer.spawn(world, pos, ((BlockCraftingPlateEntity) blockEntity).getInventory());
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        return this.getDefaultState().with(WATERLOGGED, context.getWorld().getFluidState(context.getBlockPos()).getFluid() == Fluids.WATER);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom)
    {
        if (state.get(WATERLOGGED))
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

    @Override public FluidState getFluidState(BlockState state) { return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state); }
    
    @Override protected void appendProperties(StateManager.Builder<Block, BlockState> builder) { builder.add(WATERLOGGED); }

    @Override public ManaNodeType getManaNodeType() { return ManaNodeType.SINK; }
}