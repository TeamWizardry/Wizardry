package com.teamwizardry.wizardry.common.spell;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.teamwizardry.wizardry.api.spell.Pattern;
import com.teamwizardry.wizardry.api.spell.TargetType;
import com.teamwizardry.wizardry.common.spell.component.Instance;

import net.minecraft.nbt.CompoundNBT;

public class ShapeInstance extends Instance
{
    private ShapeInstance next;
    private List<EffectInstance> effects;

    public ShapeInstance(Pattern pattern, TargetType targetType, Map<String, Double> attributeValues, double manaCost, double burnoutCost)
    {
        super(pattern, targetType, attributeValues, manaCost, burnoutCost);
        this.effects = new LinkedList<>();
    }
    
    public ShapeInstance setNext(ShapeInstance next) { this.next = next; return this; }
    
    public ShapeInstance addEffect(EffectInstance effect) { effects.add(effect); return this; }
    
    public void run()
    {
        this.pattern.run(null, new CompoundNBT(), this.targetType);
    }
}
