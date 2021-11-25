package com.teamwizardry.wizardry.common.network

import com.teamwizardry.wizardry.common.block.IManaNode
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import kotlin.math.min

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
        if (sink !is IManaNode || (sink as IManaNode).manaNodeType != IManaNode.ManaNodeType.SINK) return true
        val sourcePos = nodes[nodes.size - 1]
        val sourceState: BlockState = world.getBlockState(sourcePos)
        val source = sourceState.block
        if (source !is IManaNode || (source as IManaNode).manaNodeType != IManaNode.ManaNodeType.SOURCE) return true
        for (i in 1 until nodes.size - 1) {
            val node = nodes[i]
            val state: BlockState = world.getBlockState(node)
            val block = state.block
            if (block !is IManaNode || (block as IManaNode).manaNodeType != IManaNode.ManaNodeType.ROUTER) return true
        }
        val sourceNode: IManaNode = source
        val sinkNode: IManaNode = sink
        val manaToTransfer: Double = min(sourceNode.getMana(world, sourcePos), sinkNode.getMissingMana(world, sinkPos))
        if (manaToTransfer > 0) {
            sourceNode.transferMana(world, sourcePos, sinkPos, manaToTransfer)
            return true
        }
        return false
    }
}