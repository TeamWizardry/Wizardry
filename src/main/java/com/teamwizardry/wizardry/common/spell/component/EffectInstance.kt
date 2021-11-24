package com.teamwizardry.wizardry.common.spell.component

/**
 * Contains data relevant to a single cast event of a `ModuleEffect` component.
 * Do not construct, instances are provided for calls to
 * [PatternEffect.affectBlock] and
 * [PatternEffect.affectEntity]
 * See [Instance] for detailed information on the available data.
 * @see Instance
 *
 * @see PatternEffect
 */
class EffectInstance(
    pattern: Pattern?, targetType: TargetType, attributeValues: Map<String?, Double>, manaCost: Double,
    burnoutCost: Double, caster: Interactor
) : Instance(pattern, targetType, attributeValues, manaCost, burnoutCost, caster) {
    init {
        if (extraData == null) {
            extraData = NbtCompound()
        }
        extraData.putString(PATTERN_TYPE, "effect")
    }
}