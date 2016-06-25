package com.teamwizardry.wizardry.schematic;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

/**
 * Created by Saad on 6/10/2016.
 */
public class BlockObject {

    private BlockPos pos;
    private IBlockState state;

    BlockObject(BlockPos pos, IBlockState state) {
        this.pos = pos;
        this.state = state;
    }

    public BlockPos getPos() {
        return pos;
    }

    public IBlockState getState() {
        return state;
    }
}