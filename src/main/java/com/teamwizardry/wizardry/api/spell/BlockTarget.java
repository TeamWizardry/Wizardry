package com.teamwizardry.wizardry.api.spell;

import java.util.List;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;

public class BlockTarget implements ITargetComponent<Block> {
    public static final Function<BlockPos, Boolean> ALWAYS = block -> true;
    public static final Function<BlockPos, Boolean> NEVER = block -> false;

    private final Function<Block, Boolean> targetFunction;
    private final String name;
    private final List<Item> items;

    private BlockTarget(String name, List<Item> items, Function<Block, Boolean> function) {
        this.name = name;
        this.items = items;
        this.targetFunction = function;
    }

    @Override
    public String getName()
    {
        return name;
    }
    
    @Override
    public List<Item> getItems()
    {
        return items;
    }

    public boolean apply(Block block)
    {
        return targetFunction.apply(block);
    }
}
