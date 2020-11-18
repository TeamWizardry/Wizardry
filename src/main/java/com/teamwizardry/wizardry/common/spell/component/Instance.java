package com.teamwizardry.wizardry.common.spell.component;

import java.util.Map;

import com.teamwizardry.wizardry.api.spell.Interactor;
import com.teamwizardry.wizardry.api.spell.Pattern;
import com.teamwizardry.wizardry.api.spell.TargetType;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

public abstract class Instance
{
    protected final Pattern pattern;
    protected final TargetType targetType;
    protected final Map<String, Double> attributeValues;
    protected final double manaCost;
    protected final double burnoutCost;
    
    protected final Interactor caster;
    protected final CompoundNBT extraData;
    
    public Instance(Pattern pattern, TargetType targetType, Map<String, Double> attributeValues, double manaCost, double burnoutCost, Interactor caster)
    {
        this.pattern = pattern;
        this.targetType = targetType;
        this.attributeValues = attributeValues;
        this.manaCost = manaCost;
        this.burnoutCost = burnoutCost;
        
        this.caster = caster;
        this.extraData = new CompoundNBT();
    }
    
    public void run(World world, Interactor source, Interactor target)
    {
        this.pattern.run(world, this.caster, source, target, this.attributeValues, this.manaCost, this.burnoutCost);
    }
    
    public Pattern getPattern() { return this.pattern; }
    
    public TargetType getTargetType() { return this.targetType; }
    
    public double getAttributeValue(String attribute) { return this.attributeValues.getOrDefault(attribute, 1.0); }
    
    public double getManaCost() { return this.manaCost; }
    
    public double getBurnoutCost() { return this.burnoutCost; }
    
     
}
