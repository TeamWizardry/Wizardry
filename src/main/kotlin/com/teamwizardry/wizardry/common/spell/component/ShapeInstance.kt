package com.teamwizardry.wizardry.common.spell.component

class ShapeInstance(
    pattern: Pattern,
    targetType: TargetType,
    attributeValues: Map<String, Double>,
    manaCost: Double,
    caster: Interactor
) : Instance(pattern, targetType, attributeValues, manaCost, caster) {
    init {
        extraData.putString("pattern_type", "shape")
    }
}