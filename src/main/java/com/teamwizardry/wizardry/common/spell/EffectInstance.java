package com.teamwizardry.wizardry.common.spell;

import java.util.Map;

import com.teamwizardry.wizardry.api.spell.Interactor;
import com.teamwizardry.wizardry.api.spell.Pattern;
import com.teamwizardry.wizardry.api.spell.TargetType;
import com.teamwizardry.wizardry.common.spell.component.Instance;

public class EffectInstance extends Instance
{
    public EffectInstance(Pattern pattern, TargetType targetType, Map<String, Double> attributeValues, double manaCost, double burnoutCost, Interactor caster)
    {
        super(pattern, targetType, attributeValues, manaCost, burnoutCost, caster);
    }
}
