package com.teamwizardry.wizardry.common.spell.component

class ShapeInstance(
    pattern: Pattern?,
    targetType: TargetType,
    attributeValues: Map<String?, Double>,
    manaCost: Double,
    burnoutCost: Double,
    caster: Interactor
) : Instance(pattern, targetType, attributeValues, manaCost, burnoutCost, caster) {
    init {
        extraData.putString(PATTERN_TYPE, "shape")
    }
}