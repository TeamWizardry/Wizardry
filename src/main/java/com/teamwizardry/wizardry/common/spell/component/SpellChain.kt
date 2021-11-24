package com.teamwizardry.wizardry.common.spell.component

import java.util.function.Consumer

abstract class SpellChain // implements INBTSerializable<NbtCompound>
    (@field:Save protected var module: Module?) {
    @Save
    protected var targetType: TargetType = TargetType.ALL

    @Save
    protected var modifiers: MutableMap<String?, Int> = HashMap()

    @Save
    protected var manaMultiplier = 0.0
    fun addModifier(modifier: Modifier): SpellChain {
        val attribute = modifier.attribute
        modifiers.merge(attribute, 1, BiFunction<Int, Int, Int> { a: Int, b: Int -> a + b })
        manaMultiplier *= module!!.getCostPerModifier(attribute)
        return this
    }

    fun setTarget(target: TargetType): SpellChain {
        targetType = target
        return this
    }

    open fun toInstance(caster: Interactor): Instance? {
        // TODO: Get modifications from Caster (Halo, potions, autocaster tiers, etc.)
        val attributeValues: MutableMap<String?, Double> = HashMap()
        // Set the value for all unmodified values
        module.getAllAttributes().forEach(Consumer { attribute: String? ->
            attributeValues[attribute] = module!!.getAttributeValue(attribute, 0)
        })
        // Then set the modified ones with their proper totals
        modifiers.forEach { (attribute: String?, count: Int) ->
            attributeValues[attribute] = module!!.getAttributeValue(attribute, count)
        }
        if (module is ModuleShape) return ShapeInstance(
            module.getPattern(),
            targetType,
            attributeValues,
            module.getBaseManaCost() * manaMultiplier,
            module.getBaseBurnoutCost() * manaMultiplier,
            caster
        ) else if (module is ModuleEffect) return EffectInstance(
            module.getPattern(),
            targetType,
            attributeValues,
            module.getBaseManaCost() * manaMultiplier,
            module.getBaseBurnoutCost() * manaMultiplier,
            caster
        )
        throw InconceivableException("How? There are only two module types, you shouldn't ever be constructing the root")
    }

    open fun serializeNBT(): NbtCompound? {
        val nbt = NbtCompound()
        val moduleName = ComponentRegistry.getModules().entries.stream()
            .filter { (_, value): Map.Entry<String?, Module?> -> value == module }
            .map { (key): Map.Entry<String?, Module?> -> key }
            .findFirst().get()
        nbt.putString(MODULE, moduleName)
        val targetVal: String = targetType.name()
        nbt.putString(TARGET, targetVal)
        val modifiers = NbtCompound()
        this.modifiers.forEach { (key: String?, value: Int?) -> modifiers.putInt(key, value) }
        nbt.put(MODIFIERS, modifiers)
        nbt.putDouble(MULTIPLIER, manaMultiplier)
        return nbt
    }

    open fun deserializeNBT(nbt: NbtCompound) {
        module = ComponentRegistry.getModules()[nbt.getString(MODULE)]
        targetType = TargetType.valueOf(nbt.getString(TARGET))
        val modifiers: NbtCompound = nbt.getCompound(MODIFIERS)
        modifiers.getKeys()
            .forEach(Consumer { attribute: String? -> this.modifiers[attribute] = modifiers.getInt(attribute) })
        manaMultiplier = nbt.getDouble(MULTIPLIER)
    }

    companion object {
        const val MODULE = "module"
        const val TARGET = "target"
        const val MODIFIERS = "modifiers"
        const val MULTIPLIER = "multiplier"
        const val NEXT = "next"
        const val EFFECTS = "effects"
    }

}