package com.teamwizardry.wizardry.api.spell;

import java.util.Map;

public class EffectInstance extends Instance
{
    public EffectInstance(Pattern pattern, TargetType targetType, Map<String, Double> attributeValues, double manaCost, double burnoutCost, Interactor caster)
    {
        super(pattern, targetType, attributeValues, manaCost, burnoutCost, caster);
    }
}
