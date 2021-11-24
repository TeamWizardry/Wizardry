package com.teamwizardry.wizardry.common.spell.component;

import java.util.Map;

public class ShapeInstance extends Instance
{
    public ShapeInstance(Pattern pattern, TargetType targetType, Map<String, Double> attributeValues, double manaCost, double burnoutCost, Interactor caster)
    {
        super(pattern, targetType, attributeValues, manaCost, burnoutCost, caster);
        extraData.putString(PATTERN_TYPE, "shape");
    }
}
