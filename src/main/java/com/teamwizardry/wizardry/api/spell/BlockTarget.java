package com.teamwizardry.wizardry.api.spell;

import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;

public class BlockTarget implements ISpellComponent
{
    public static final Function<BlockPos, Boolean> ALWAYS = block -> true;
    public static final Function<BlockPos, Boolean> NEVER = block -> false;
    
    private Function<Block, Boolean> targetFunction;
    private String name;
    private Item item;
    
    private BlockTarget(String name, Item item, Function<Block, Boolean> function)
    {
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
