package com.teamwizardry.wizardry.common.block.entity.manabattery

import com.teamwizardry.wizardry.common.block.IManaNode
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.Waterloggable
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos

class BlockManaBattery(settings: Settings?) : BlockWithEntity(settings), Waterloggable, IManaNode {
    override val manaNodeType: IManaNode.ManaNodeType
        get() = IManaNode.ManaNodeType.SOURCE

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return BlockManaBatteryEntity(pos, state)
    } // TODO: Check if necessary, find replacement
    //	@Override
    //	public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
    //		return false;
    //	}
}