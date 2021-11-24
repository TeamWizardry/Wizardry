package com.teamwizardry.wizardry.common.network;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.block.IManaNode;

import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PersistentState;

public class ManaNetwork extends PersistentState
{
    private static final String DATA_NAME = Wizardry.MODID + "_ManaNetwork";
    private static final String POSITIONS = "positions";
    
    private Map<ChunkPos, Set<BlockPos>> positions;
    
    private List<ManaPath> paths;
    
    private ManaNetwork()
    {
        this.positions = new HashMap<>();
        this.paths = new LinkedList<>();
    }
    
    public static ManaNetwork get(ServerWorld world)
    {
        return world.getPersistentStateManager().getOrCreate(ManaNetwork::readNbt, ManaNetwork::new, DATA_NAME);
    }
    
    public void addBlock(BlockPos pos)
    {
        positions.computeIfAbsent(new ChunkPos(pos), k -> new HashSet<>()).add(pos);
    }
    
    public void removeBlock(BlockPos pos)
    {
        Set<BlockPos> blocks = positions.get(new ChunkPos(pos));
        if (blocks != null) blocks.remove(pos);
    }
    
    public void tick(ServerWorld world)
    {
        paths.removeIf(path -> !path.transfer(world));
    }
    
    public boolean findPath(ServerWorld world, BlockPos pos)
    {
        Map<BlockPos, ManaPath> paths = new HashMap<>();
        Queue<BlockPos> nodesToSearch = new LinkedList<>();
        
        for (BlockPos node : nodesNear(pos))
        {
            paths.put(node, new ManaPath(pos, node));
            nodesToSearch.add(node);
        }
        
        while (!nodesToSearch.isEmpty())
        {
            BlockPos nodePos = nodesToSearch.remove();
            Block nodeBlock = world.getBlockState(nodePos).getBlock();
            if (!(nodeBlock instanceof IManaNode))
            {
                this.positions.get(new ChunkPos(nodePos)).remove(nodePos);
                continue;
            }
            IManaNode node = (IManaNode) world.getBlockState(nodePos).getBlock();
            ManaPath path = paths.get(nodePos);
            
            switch (node.getManaNodeType())
            {
                case SINK:
                    continue;
                case SOURCE:
                    this.paths.add(path);
                    return true;
                case ROUTER:
                    for (BlockPos nearby : nodesNear(nodePos))
                    {
                        paths.put(nearby, new ManaPath(path, nearby));
                        nodesToSearch.add(nearby);
                    }
            }
        }
        
        return false;
    }
    
    private List<BlockPos> nodesNear(BlockPos pos)
    {
        double maxDist = 32; // TODO: config? variable?
        
        int chunkDist = (int) Math.ceil(maxDist / 16);
        
        List<BlockPos> nodes = new LinkedList<>();
        int centerX = pos.getX() >> 4;
        int centerZ = pos.getZ() >> 4;
        
        for (int x = -chunkDist; x <= chunkDist; x++)
            for (int z = -chunkDist; z <= chunkDist; z++)
                this.positions.getOrDefault(new ChunkPos(centerX + x, centerZ + z), Collections.emptySet()).stream()
                    .filter(node -> node.getSquaredDistance(pos) <= maxDist * maxDist).forEach(nodes::add);
        
        return nodes;
    }

    public static ManaNetwork readNbt(NbtCompound nbt)
    {
        ManaNetwork network = new ManaNetwork();
        for (long pos : nbt.getLongArray(POSITIONS))
        {
            BlockPos block = BlockPos.fromLong(pos);
            ChunkPos chunk = new ChunkPos(block);
            network.positions.computeIfAbsent(chunk, k -> new HashSet<>()).add(block);
        }
        return network;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound compound)
    {
        compound.putLongArray(POSITIONS, positions.values().stream().flatMap(Set::stream).mapToLong(BlockPos::asLong).toArray());
        return compound;
    }
}
