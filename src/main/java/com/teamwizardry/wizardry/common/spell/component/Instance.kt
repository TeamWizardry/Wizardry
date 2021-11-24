package com.teamwizardry.wizardry.common.spell.component

import net.fabricmc.api.Environment
import java.awt.Color

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
abstract class Instance(
    var pattern: Pattern?, targetType: TargetType, attributeValues: Map<String?, Double>, manaCost: Double,
    burnoutCost: Double, caster: Interactor
) {
    private var targetType: TargetType

    /**
     * List of all known attribute values. Keys found in [Attributes].
     * Use [Map.getOrDefault] to retrieve values, as
     * only attributes with values and ranges defined in Module yamls will
     * appear in here.
     * @see .getAttributeValue
     */
    private var attributeValues: Map<String?, Double>
        protected set
    private var manaCost: Double
        protected set
    private var burnoutCost: Double
        protected set
    var nextShape: ShapeInstance? = null
    var effects: MutableList<EffectInstance>
    protected var caster: Interactor
    protected var extraData: NbtCompound
    fun setNext(next: ShapeInstance?): Instance {
        nextShape = next
        return this
    }

    fun addEffect(effect: EffectInstance): Instance {
        effects.add(effect)
        return this
    }

    fun getTargetType(): TargetType {
        return targetType
    }

    /**
     * Retrieve the value for a given [Attributes]. Defaults to 1 if neither
     * value nor range were defined in the Module's yaml
     */
    fun getAttributeValue(attribute: String?): Double {
        return attributeValues[attribute] ?: 1.0
    }

    fun getCaster(): Interactor {
        return caster
    }

    fun getExtraData(): NbtCompound {
        return extraData
    }

    fun run(world: World, target: Interactor?) {
        pattern!!.run(world, this, target)
    }

    @Environment(EnvType.CLIENT)
    fun runClient(world: World?, target: Interactor?) {
        pattern!!.runClient(world, this, target)
    }

    val effectColors: List<Array<Color>>
        get() {
            val colors = effects.stream()
                .map { obj: EffectInstance -> obj.getPattern() }
                .map { obj: Pattern? -> obj.getColors() }
                .collect(Collectors.toList<Array<Color>>())
            if (nextShape != null) {
                colors.addAll(nextShape.getEffectColors())
            }
            return colors
        }

    fun toNBT(): NbtCompound {

//        if (pattern != null)
//            nbt.putString(PATTERN, pattern.getRegistryName().toString());
//
//        if (targetType != null)
//            nbt.putString(TARGET_TYPE, targetType.toString());
//
//        if (attributeValues != null) {
//            NbtCompound nbtAttributeValues = new NbtCompound();
//            attributeValues.forEach(nbtAttributeValues::putDouble);
//            nbt.put(ATTRIBUTE_VALUES, nbtAttributeValues);
//        }
//
//        nbt.putDouble(MANA_COST, manaCost);
//        nbt.putDouble(BURNOUT_COST, burnoutCost);
//
//        if (nextShape != null)
//            nbt.put(NEXT_SHAPE, nextShape.toNBT());
//
//        if (!effects.isEmpty()) {
//            NbtCompound nbtEffects = new NbtCompound();
//            for (EffectInstance instance : effects)
//                nbtEffects.put(effects.indexOf(instance) + "", instance.toNBT());
//            nbt.put(EFFECTS, nbtEffects);
//        }
//
//        nbt.put(CASTER, caster.toNBT());
//        nbt.put(EXTRA_DATA, extraData);
        return NbtCompound()
    }

    companion object {
        const val PATTERN_TYPE = "pattern_type"
        fun fromNBT(world: World?, nbt: NbtCompound?): Instance? {
            var pattern: Pattern
            var targetType: TargetType
            val attributeValues: Map<String, Double>? = null
            val manaCost = 0.0
            val burnoutCost = 0.0
            val nextShape: ShapeInstance? = null
            val effects: List<EffectInstance> = LinkedList<EffectInstance>()
            var caster: Interactor
            val extraData = NbtCompound()

//        if (nbt.contains(PATTERN))
//            pattern = GameRegistry.findRegistry(Pattern.class).getValue(new Identifier(nbt.getString(PATTERN)));
//        else throw new InconceivableException("Pattern must always be specified.");

//        if (nbt.contains(TARGET_TYPE))
//            targetType = TargetType.valueOf(nbt.getString(TARGET_TYPE));
//        else throw new InconceivableException("TargetType enum must always be specified.");

//        if (nbt.contains(ATTRIBUTE_VALUES)) {
//            NbtCompound nbtAttributeValues = nbt.getCompound(ATTRIBUTE_VALUES);
//            attributeValues = new HashMap<>();
//            for (String key : nbtAttributeValues.keySet())
//                attributeValues.put(key, nbtAttributeValues.getDouble(key));
//        }

//        if (nbt.contains(MANA_COST))
//            manaCost = nbt.getDouble(MANA_COST);

//        if (nbt.contains(BURNOUT_COST))
//            burnoutCost = nbt.getDouble(BURNOUT_COST);

//        if (nbt.contains(NEXT_SHAPE))
//            nextShape = (ShapeInstance) fromNBT(world, nbt.getCompound(NEXT_SHAPE));

//        if (nbt.contains(EFFECTS)) {
//            NbtCompound nbtEffects = nbt.getCompound(EFFECTS);
//            effects = new ArrayList<>();
//            for (int i = 0; i < nbtEffects.size(); i++)
//                effects.add((EffectInstance) fromNBT(world, nbtEffects.getCompound(i + "")));
//        }
//        if (nbt.contains(CASTER))
//            caster = Interactor.fromNBT(world, nbt.getCompound(CASTER));
//        else throw new InconceivableException("Caster Interactor must always be specified.");

//        if (nbt.contains(EXTRA_DATA))
//            extraData = nbt.getCompound(EXTRA_DATA);

//        Instance instance;
//        if (extraData != null && extraData.contains(PATTERN_TYPE)) {
//            String patternType = extraData.getString(PATTERN_TYPE);
//            if (patternType.equals("shape"))
//                instance = new ShapeInstance(pattern, targetType, attributeValues, manaCost, burnoutCost, caster);
//            else if (patternType.equals("effect"))
//                instance = new EffectInstance(pattern, targetType, attributeValues, manaCost, burnoutCost, caster);
//            else throw new InconceivableException("Pattern type must be Shape or Effect");
//            instance.nextShape = nextShape;
//            instance.effects = effects;
//            instance.extraData = extraData;
//        } else throw new InconceivableException("Pattern type must always be specified.");

//        return instance;
            return null
        }
    }

    init {
        this.targetType = targetType
        this.attributeValues = attributeValues
        this.manaCost = manaCost
        this.burnoutCost = burnoutCost
        this.caster = caster
        extraData = NbtCompound()
        effects = LinkedList<EffectInstance>()
    }
}