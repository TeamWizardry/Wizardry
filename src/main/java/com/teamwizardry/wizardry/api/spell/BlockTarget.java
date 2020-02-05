package com.teamwizardry.wizardry.api.spell;

import java.util.function.Function;

import net.minecraft.util.math.BlockPos;

public class BlockTarget
{
    private BlockTarget() {}
    
    public static final Function<BlockPos, Boolean> ALWAYS = entity -> true;
    public static final Function<BlockPos, Boolean> NEVER = entity -> false;
}
