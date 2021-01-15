package com.teamwizardry.wizardry.common.network;

import java.util.ArrayList;
import java.util.List;

import com.teamwizardry.wizardry.api.block.IManaNode;
import com.teamwizardry.wizardry.api.block.IManaNode.ManaNodeType;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ManaPath
{
    private List<BlockPos> nodes;
    
    public ManaPath(BlockPos... nodes)
    {
        this.nodes = new ArrayList<>();
        for (BlockPos node : nodes)
            this.nodes.add(node);
    }
    
    public ManaPath(ManaPath path, BlockPos... nodes)
    {
        this.nodes = new ArrayList<>();
        this.nodes.addAll(path.nodes);
        for (BlockPos node : nodes)
            this.nodes.add(node);
    }
    
    public boolean transfer(World world)
    {
        BlockPos sinkPos = nodes.get(0);
        BlockState sinkState = world.getBlockState(sinkPos);
        Block sink = sinkState.getBlock();
        if (!(sink instanceof IManaNode) || ((IManaNode) sink).getManaNodeType() != ManaNodeType.SINK)
                return true;
        
        BlockPos sourcePos = nodes.get(nodes.size()-1);
        BlockState sourceState = world.getBlockState(sourcePos);
        Block source = sourceState.getBlock();
        if (!(source instanceof IManaNode) || ((IManaNode) source).getManaNodeType() != ManaNodeType.SOURCE)
                return true;
        
        for (int i = 1; i < nodes.size()-1; i++)
        {
            BlockPos node = nodes.get(i);
            BlockState state = world.getBlockState(node);
            Block block = state.getBlock();
            if (!(block instanceof IManaNode) || ((IManaNode) block).getManaNodeType() != ManaNodeType.ROUTER)
                return true;
        }
        
        IManaNode sourceNode = (IManaNode) source;
        IManaNode sinkNode = (IManaNode) sink;
        
        double manaToTransfer = Math.min(sourceNode.getMana(world, sourcePos), sinkNode.getMissingMana(world, sinkPos));
        
        if (manaToTransfer > 0)
        {
            sourceNode.transferMana(world, sourcePos, sinkPos, manaToTransfer);
            return true;
        }
        return false;
    }
}
