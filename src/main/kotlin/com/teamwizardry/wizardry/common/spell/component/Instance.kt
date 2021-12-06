package com.teamwizardry.wizardry.common.spell.component

import com.teamwizardry.librarianlib.core.util.kotlin.InconceivableException
import com.teamwizardry.wizardry.common.init.ModPatterns
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import net.minecraft.world.World
import java.awt.Color
import java.util.*
import java.util.stream.Collectors

/**
 * Contains data relevant to a single cast event of a `Module`
 * Do not construct, instances are provided for calls to
 * [Pattern.affectBlock] and
 * [Pattern.affectEntity].
 * @see EffectInstance
 *
 * @see ShapeInstance
 *
 * @see PatternEffect
 *
 * @see PatternShape
 */
abstract class Instance(val pattern: Pattern, val targetType: TargetType, val attributeValues: Map<String, Double>, val manaCost: Double, val caster: Interactor) {
    /**
     * List of all known attribute values. Keys found in [Attributes].
     * Use [Map.getOrDefault] to retrieve values, as
     * only attributes with values and ranges defined in Module yamls will
     * appear in here.
     * @see .getAttributeValue
     */
    var nextShape: ShapeInstance? = null
        protected set
    var effects: MutableList<EffectInstance>
        protected set
    var extraData: NbtCompound
        protected set
    fun setNext(next: ShapeInstance): Instance {
        nextShape = next
        return this
    }

    fun addEffect(effect: EffectInstance): Instance {
        effects.add(effect)
        return this
    }

    /**
     * Retrieve the value for a given [Attributes]. Defaults to 1 if neither
     * value nor range were defined in the Module's yaml
     */
    fun getAttributeValue(attribute: String?): Double {
        return attributeValues[attribute] ?: 1.0
    }

    fun run(world: World, target: Interactor) {
        pattern.run(world, this, target)
    }

    @Environment(EnvType.CLIENT)
    fun runClient(world: World, target: Interactor) {
        pattern.runClient(world, this, target)
    }

    val effectColors: List<Array<Color>>
        get() {
            val colors = effects.stream()
                .map { obj: EffectInstance -> obj.pattern }
                .map { obj: Pattern -> obj.colors }
                .collect(Collectors.toList())
            if (nextShape != null) {
                colors.addAll(nextShape!!.effectColors)
            }
            return colors
        }

    fun toNBT(): NbtCompound {
        val nbt = NbtCompound()

        nbt.putString(PATTERN, pattern.id.toString());

        nbt.putString(TARGET_TYPE, targetType.toString());

        val nbtAttributeValues = NbtCompound();
        attributeValues.forEach(nbtAttributeValues::putDouble);
        nbt.put(ATTRIBUTE_VALUES, nbtAttributeValues);

        nbt.putDouble(MANA_COST, manaCost);

        if (nextShape != null)
            nbt.put(NEXT_SHAPE, nextShape!!.toNBT());

        val nbtEffects = NbtCompound();
        for (instance in effects)
            nbtEffects.put("${effects.indexOf(instance)}", instance.toNBT());
        nbt.put(EFFECTS, nbtEffects);

        nbt.put(CASTER, caster.toNBT());
        nbt.put(EXTRA_DATA, extraData);
        return nbt
    }

    companion object {
        private const val PATTERN = "pattern"
        private const val PATTERN_TYPE = "pattern_type"
        private const val TARGET_TYPE = "target_type"
        private const val ATTRIBUTE_VALUES = "attribute_values"
        private const val MANA_COST = "mana_cost"
        private const val NEXT_SHAPE = "next_shape"
        private const val EFFECTS = "effects"
        private const val CASTER = "caster"
        private const val EXTRA_DATA = "extra_data"

        fun fromNBT(world: World, nbt: NbtCompound): Instance? {
            val pattern = ModPatterns.PATTERN[Identifier(nbt.getString(PATTERN))]
            val targetType = TargetType.valueOf(nbt.getString(TARGET_TYPE))
            val manaCost = nbt.getDouble(MANA_COST)
            val nextShape = if (nbt.contains(NEXT_SHAPE)) fromNBT(world, nbt.getCompound(NEXT_SHAPE)) as ShapeInstance else null
            val caster = Interactor.fromNBT(world, nbt.getCompound(CASTER)) ?: return null
            val extraData = nbt.getCompound(EXTRA_DATA)

            val attributeValues = HashMap<String, Double>()
            val nbtAttributeValues = nbt.getCompound(ATTRIBUTE_VALUES)
            nbtAttributeValues.keys.forEach{ attributeValues[it] = nbtAttributeValues.getDouble(it) }

            val effects = ArrayList<EffectInstance>()
            val nbtEffects = nbt.getCompound(EFFECTS)
            nbtEffects.keys.forEach{ effects.add(fromNBT(world, nbtEffects.getCompound(it)) as EffectInstance) }

            if (extraData.contains(PATTERN_TYPE)) {
                val instance = when (extraData.getString(PATTERN_TYPE)) {
                    "shape" -> ShapeInstance(pattern, targetType, attributeValues, manaCost, caster)
                    "effect" -> EffectInstance(pattern, targetType, attributeValues, manaCost, caster)
                    else -> throw InconceivableException("Pattern type must be Shape or Effect")
                }
                instance.nextShape = nextShape
                instance.effects = effects
                instance.extraData = extraData
                return instance
            } else throw InconceivableException("Pattern type must always be specified.")
        }
    }

    init {
        extraData = NbtCompound()
        effects = LinkedList<EffectInstance>()
    }
}