package com.teamwizardry.wizardry.common.spell.component;

import java.util.Map;

import com.teamwizardry.wizardry.api.spell.Pattern;
import com.teamwizardry.wizardry.api.spell.TargetType;

public abstract class Instance
{
    protected final Pattern pattern;
    protected final TargetType targetType;
    protected final Map<String, Double> attributeValues;
    protected final double manaCost;
    protected final double burnoutCost;
    
    public Instance(Pattern pattern, TargetType targetType, Map<String, Double> attributeValues, double manaCost, double burnoutCost)
    {
        this.pattern = pattern;
        this.targetType = targetType;
        this.attributeValues = attributeValues;
        this.manaCost = manaCost;
        this.burnoutCost = burnoutCost;
    }
}
