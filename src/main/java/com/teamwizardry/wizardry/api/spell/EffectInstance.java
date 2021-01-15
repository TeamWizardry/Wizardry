package com.teamwizardry.wizardry.api.spell;

import net.minecraft.nbt.CompoundNBT;

import java.util.Map;

import static com.teamwizardry.wizardry.api.StringConsts.PATTERN_TYPE;

/**
 * Contains data relevant to a single cast event of a {@code ModuleEffect} component.
 * Do not construct, instances are provided for calls to
 * {@link PatternEffect#affectBlock(net.minecraft.world.World, Interactor, Instance)} and
 * {@link PatternEffect#affectEntity(net.minecraft.world.World, Interactor, Instance)}
 * See {@link Instance} for detailed information on the available data.
 * @see Instance
 * @see PatternEffect
 */
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
