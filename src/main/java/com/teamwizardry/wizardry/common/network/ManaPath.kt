package com.teamwizardry.wizardry.common.network

import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos

class ManaPath {
    private var nodes: MutableList<BlockPos>

    constructor(vararg nodes: BlockPos) {
        this.nodes = ArrayList()
        for (node in nodes) this.nodes.add(node)
    }

    constructor(path: ManaPath?, vararg nodes: BlockPos) {
        this.nodes = ArrayList()
        this.nodes.addAll(path!!.nodes)
        for (node in nodes) this.nodes.add(node)
    }

    fun transfer(world: World): Boolean {
        val sinkPos = nodes[0]
        val sinkState: BlockState = world.getBlockState(sinkPos)
        val sink = sinkState.block
        if (sink !is IManaNode || (sink as IManaNode).getManaNodeType() != ManaNodeType.SINK) return true
        val sourcePos = nodes[nodes.size - 1]
        val sourceState: BlockState = world.getBlockState(sourcePos)
        val source = sourceState.block
        if (source !is IManaNode || (source as IManaNode).getManaNodeType() != ManaNodeType.SOURCE) return true
        for (i in 1 until nodes.size - 1) {
            val node = nodes[i]
            val state: BlockState = world.getBlockState(node)
            val block = state.block
            if (block !is IManaNode || (block as IManaNode).getManaNodeType() != ManaNodeType.ROUTER) return true
        }
        val sourceNode: IManaNode = source as IManaNode
        val sinkNode: IManaNode = sink as IManaNode
        val manaToTransfer: Double =
            Math.min(sourceNode.getMana(world, sourcePos), sinkNode.getMissingMana(world, sinkPos))
        if (manaToTransfer > 0) {
            sourceNode.transferMana(world, sourcePos, sinkPos, manaToTransfer)
            return true
        }
        return false
    }
}