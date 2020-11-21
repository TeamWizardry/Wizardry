package com.teamwizardry.wizardry.api.spell;

import net.minecraft.nbt.CompoundNBT;

import java.util.Map;

import static com.teamwizardry.wizardry.api.StringConsts.PATTERN_TYPE;

public class EffectInstance extends Instance {
    public EffectInstance(Pattern pattern, TargetType targetType, Map<String, Double> attributeValues, double manaCost,
                          double burnoutCost, Interactor caster) {
        super(pattern, targetType, attributeValues, manaCost, burnoutCost, caster);
        if (extraData == null) {
            extraData = new CompoundNBT();
        }
        extraData.putString(PATTERN_TYPE, "effect");
    }
}
