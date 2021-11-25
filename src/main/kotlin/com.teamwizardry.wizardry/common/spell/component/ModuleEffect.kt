package com.teamwizardry.wizardry.common.spell.component

import net.minecraft.item.Item

class ModuleEffect(
    pattern: PatternEffect,
    name: String,
    items: List<Item>,
    baseManaCost: Double,
    baseBurnoutCost: Double,
    val action: String,
    element: String,
    modifierCosts: Map<String, Double>,
    attributeValues: Map<String, List<Double>>
) : Module(pattern, name, items, baseManaCost, baseBurnoutCost, element, modifierCosts, attributeValues) {
    override val pattern: Pattern = pattern
        get() = field as PatternEffect
}