package com.teamwizardry.wizardry.common.spell;

import java.util.HashMap;
import java.util.Map;

import com.teamwizardry.wizardry.api.spell.TargetType;

import net.minecraft.nbt.CompoundNBT;

public abstract class SpellChain
{
    protected Module module;
    protected TargetType targetType = TargetType.ALL;
    protected Map<String, Integer> modifiers;
    protected double manaMultiplier;
    
    public SpellChain(Module module)
    {
        this.module = module;
        this.modifiers = new HashMap<>();
    }
    
    public SpellChain addModifier(Modifier modifier)
    {
        modifier.getAttributeModifiers().entrySet().forEach(entry -> {
            String attribute = entry.getKey();
            int count = entry.getValue();
            modifiers.merge(attribute, count, (a,b) -> a+b);
            manaMultiplier *= Math.pow(module.getCostPerModifier(attribute), count);
        });
        return this;
    }
    
    public SpellChain setTarget(TargetType target) { this.targetType = target; return this; }
    
    public void run()
    {
        // TODO: Apply equipment and potion modifiers
        
        module.getPattern().run(null, new CompoundNBT(), targetType);
    }
}
