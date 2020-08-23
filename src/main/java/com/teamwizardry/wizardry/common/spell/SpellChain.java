package com.teamwizardry.wizardry.common.spell;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.teamwizardry.wizardry.api.spell.BlockTarget;
import com.teamwizardry.wizardry.api.spell.EntityTarget;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public abstract class SpellChain
{
    protected Module module;
    protected Function<Entity, Boolean> shouldAffectEntity = EntityTarget.ALWAYS;
    protected Function<BlockPos, Boolean> shouldAffectBlock = BlockTarget.ALWAYS;
    protected Map<String, Integer> modifiers;
    
    public SpellChain(Module module)
    {
        this.module = module;
        this.modifiers = new HashMap<>();
        // TODO: Calculate attributes
    }
    
    public SpellChain addModifier(Modifier modifier)
    {
        modifier.getAffectedAttributes().forEach(attribute -> modifiers.merge(attribute, 1, (a,b) -> a + b));
        return this;
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
        // TODO: Apply equipment and potion modifiers
        
        module.getPattern().run(shouldAffectBlock, shouldAffectEntity);
    }
}
