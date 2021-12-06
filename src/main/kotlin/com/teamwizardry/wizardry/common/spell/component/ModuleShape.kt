package com.teamwizardry.wizardry.common.spell.component

import net.minecraft.item.Item

class ModuleShape(
    pattern: PatternShape,
    name: String,
    items: List<Item>,
    baseManaCost: Double,
    modifierCosts: Map<String, Double>,
    attributeValues: Map<String, List<Double>>
) : Module(pattern, name, items, baseManaCost, modifierCosts, attributeValues) {
    override val pattern: Pattern = pattern
        get() = field as PatternShape
}