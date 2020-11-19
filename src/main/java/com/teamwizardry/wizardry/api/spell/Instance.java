package com.teamwizardry.wizardry.api.spell;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

public abstract class Instance
{
    protected final Pattern pattern;
    protected final TargetType targetType;
    protected final Map<String, Double> attributeValues;
    protected final double manaCost;
    protected final double burnoutCost;
    
    protected ShapeInstance nextShape;
    protected List<EffectInstance> effects;
    
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
        
        this.effects = new LinkedList<>();
    }
    
    public Instance setNext(ShapeInstance next) { this.nextShape = next; return this; }
    public Instance addEffect(EffectInstance effect) { this.effects.add(effect); return this; }
    
    public void run(World world, Interactor target)
    {
        this.pattern.run(world, this, target);
    }
    
    public Pattern getPattern() { return this.pattern; }
    
    public TargetType getTargetType() { return this.targetType; }
    
    public Map<String, Double> getAttributeValues() { return this.attributeValues; }
    
    public double getAttributeValue(String attribute) { return this.attributeValues.getOrDefault(attribute, 1.0); }
    
    public double getManaCost() { return this.manaCost; }
    
    public double getBurnoutCost() { return this.burnoutCost; }
    
    public Interactor getCaster() { return this.caster; }
}
