package com.teamwizardry.wizardry.common.block.entity.manabattery;

import com.teamwizardry.wizardry.common.init.ModBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class BlockManaBatteryEntity extends BlockEntity
{
    public BlockManaBatteryEntity(BlockPos pos, BlockState state)
    {
        super(ModBlocks.manaBatteryEntity, pos, state); // TODO - give actual arguments
    }
}
