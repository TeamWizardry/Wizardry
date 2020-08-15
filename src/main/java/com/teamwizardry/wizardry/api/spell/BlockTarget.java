package com.teamwizardry.wizardry.api.spell;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;

import java.util.function.Function;

public class BlockTarget implements ITargetComponent<Block> {
    public static final Function<BlockPos, Boolean> ALWAYS = block -> true;
    public static final Function<BlockPos, Boolean> NEVER = block -> false;

    private final Function<Block, Boolean> targetFunction;
    private final String name;
    private final Item item;

    private BlockTarget(String name, Item item, Function<Block, Boolean> function) {
        this.name = name;
        this.item = item;
        this.targetFunction = function;
    }

    @Override
    public String getName()
    {
        return name;
    }
    
    @Override
    public Item getItem()
    {
        return item;
    }

    public boolean apply(Block block)
    {
        return targetFunction.apply(block);
    }
}
