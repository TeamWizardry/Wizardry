package com.teamwizardry.wizardry.common.spell;

import java.util.function.Function;

import com.teamwizardry.wizardry.api.spell.BlockTarget;
import com.teamwizardry.wizardry.api.spell.EntityTarget;
import com.teamwizardry.wizardry.api.spell.Pattern;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public abstract class SpellChain
{
    protected Pattern pattern;
    protected Function<Entity, Boolean> shouldAffectEntity = EntityTarget.ALWAYS;
    protected Function<BlockPos, Boolean> shouldAffectBlock = BlockTarget.ALWAYS;
    
    public SpellChain(Pattern pattern)
    {
        this.pattern = pattern;
    }
    
    public SpellChain setShouldAffectEntity(Function<Entity, Boolean> shouldAffectEntity)
    {
        this.shouldAffectEntity = shouldAffectEntity;
        return this;
    }
    
    public SpellChain setShouldAffectBlock(Function<BlockPos, Boolean> shouldAffectBlock)
    {
        this.shouldAffectBlock = shouldAffectBlock;
        return this;
    }
    
    public void run()
    {
        pattern.run(shouldAffectBlock, shouldAffectEntity);
    }
}
