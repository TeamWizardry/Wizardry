package com.teamwizardry.wizardry.common.block.entity.manabattery

import net.minecraft.block.AbstractBlock
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos

class BlockManaBattery(settings: AbstractBlock.Settings?) : BlockWithEntity(settings), Waterloggable, IManaNode {
    val manaNodeType: ManaNodeType
        get() = ManaNodeType.SOURCE

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return BlockManaBatteryEntity(pos, state)
    } // TODO: Check if necessary, find replacement
    //	@Override
    //	public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
    //		return false;
    //	}
}