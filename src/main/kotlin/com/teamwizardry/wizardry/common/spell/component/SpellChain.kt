package com.teamwizardry.wizardry.common.spell.component

import com.teamwizardry.librarianlib.core.util.kotlin.InconceivableException
import com.teamwizardry.librarianlib.scribe.Save
import net.minecraft.nbt.NbtCompound
import java.util.function.Consumer

abstract class SpellChain(@field:Save protected var module: Module?) {
    @Save
    protected var targetType: TargetType = TargetType.ALL

    @Save
    protected var modifiers: MutableMap<String, Int> = HashMap()

    @Save
    protected var manaMultiplier = 0.0
    fun addModifier(modifier: Modifier): SpellChain {
        val attribute = modifier.name
        modifiers.merge(attribute, 1) {a: Int, b: Int -> a + b}
        manaMultiplier *= module?.getCostPerModifier(attribute) ?: 0.05
        return this
    }

    fun setTarget(target: TargetType): SpellChain {
        targetType = target
        return this
    }

    open fun toInstance(caster: Interactor): Instance? {
        if (module == null) return null
        val module = module!!

        // TODO: Get modifications from Caster (Halo, potions, autocaster tiers, etc.)
        val attributeValues: MutableMap<String, Double> = HashMap()
        // Set the value for all unmodified values
        module.allAttributes.forEach(Consumer { attribute: String ->
            attributeValues[attribute] = module.getAttributeValue(attribute, 0)
        })
        // Then set the modified ones with their proper totals
        modifiers.forEach { (attribute: String, count: Int) ->
            attributeValues[attribute] = module.getAttributeValue(attribute, count)
        }
        if (module is ModuleShape) return ShapeInstance(
            module.pattern,
            targetType,
            attributeValues,
            module.baseManaCost * manaMultiplier,
            module.baseBurnoutCost * manaMultiplier,
            caster
        ) else if (module is ModuleEffect) return EffectInstance(
            module.pattern,
            targetType,
            attributeValues,
            module.baseManaCost * manaMultiplier,
            module.baseBurnoutCost * manaMultiplier,
            caster
        )
        throw InconceivableException("How? There are only two module types, you shouldn't ever be constructing the root")
    }

    open fun serializeNBT(): NbtCompound {
        val nbt = NbtCompound()
        val moduleName = ComponentRegistry.modules.entries.stream()
            .filter { (_, value): Map.Entry<String?, Module?> -> value == module }
            .map { (key): Map.Entry<String?, Module?> -> key }
            .findFirst().get()
        nbt.putString(MODULE, moduleName)
        val targetVal: String = targetType.name
        nbt.putString(TARGET, targetVal)
        val modifiers = NbtCompound()
        this.modifiers.forEach { (key: String?, value: Int?) -> modifiers.putInt(key, value) }
        nbt.put(MODIFIERS, modifiers)
        nbt.putDouble(MULTIPLIER, manaMultiplier)
        return nbt
    }

    open fun deserializeNBT(nbt: NbtCompound) {
        module = ComponentRegistry.modules[nbt.getString(MODULE)] ?: throw RuntimeException()
        targetType = TargetType.valueOf(nbt.getString(TARGET))
        val modifiers: NbtCompound = nbt.getCompound(MODIFIERS)
        modifiers.keys
            .forEach(Consumer { attribute: String -> this.modifiers[attribute] = modifiers.getInt(attribute) })
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