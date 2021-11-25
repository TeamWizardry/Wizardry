package com.teamwizardry.wizardry.common.spell.component

import net.minecraft.item.Item
import java.util.*
import java.util.stream.Collectors

open class Module(// Identifying data - must be unique
    open val pattern: Pattern,
    override val name: String,
    override val items: List<Item>, // Base Costs
    val baseManaCost: Double,
    val baseBurnoutCost: Double, // Modifier and Usage Metadata
    val element: String,
    private val modifierCosts: Map<String, Double>, //    public String toString() { return pattern.getRegistryName() + ":" + name + " = [" + items + ", " + element + "]"; }
    private val attributeValues: Map<String, List<Double>>
) : ISpellComponent {

//    public String getTranslationKey() { return "wizardry.spell." + pattern.getRegistryName() + ":" + name; }
//    public String getTranslationKey(String key) { return getTranslationKey() + "." + key; }

    fun getCostPerModifier(modifier: String): Double {
        return modifierCosts[modifier] ?: 0.05
    }

    fun getAttributeValue(attribute: String, count: Int): Double {
        var count = count
        val values = attributeValues.getOrDefault(attribute, listOf(1.0))
        if (count < 0) count = 0
        if (count >= values.size) count = values.size - 1
        return values[count]
    }

    /**
     * All attributes used by this module (or that at least have a non-default value)
     */
    val allAttributes: List<String>
        get() = LinkedList(attributeValues.keys)

    /**
     * All attributes that will use modifiers. Attributes with just a single value are
     * non-default, but unmodifiable, and as such should not be available for having modifiers
     */
    val attributes: List<String>
        get() = attributeValues.keys.stream().filter { attribute: String -> attributeValues[attribute]!!.size > 1 }.collect(Collectors.toList())
}