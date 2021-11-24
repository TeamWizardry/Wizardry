package com.teamwizardry.wizardry.common.spell.component;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

/**
 * Contains data relevant to a single cast event of a {@code Module}
 * Do not construct, instances are provided for calls to
 * {@link Pattern#affectBlock(World, Interactor, Instance)} and
 * {@link Pattern#affectEntity(World, Interactor, Instance)}.
 * @see EffectInstance
 * @see ShapeInstance
 * @see PatternEffect
 * @see PatternShape
 */
public abstract class Instance {
    public static final String PATTERN_TYPE = "pattern_type";
    
    protected Pattern pattern;
    protected TargetType targetType;
    protected Map<String, Double> attributeValues;
    protected double manaCost;
    protected double burnoutCost;

    protected ShapeInstance nextShape;
    protected List<EffectInstance> effects;

    protected Interactor caster;
    protected NbtCompound extraData;

    public Instance(Pattern pattern, TargetType targetType, Map<String, Double> attributeValues, double manaCost,
                    double burnoutCost, Interactor caster) {
        this.pattern = pattern;
        this.targetType = targetType;
        this.attributeValues = attributeValues;
        this.manaCost = manaCost;
        this.burnoutCost = burnoutCost;

        this.caster = caster;
        this.extraData = new NbtCompound();

        this.effects = new LinkedList<>();
    }

    public Instance setNext(ShapeInstance next) {
        this.nextShape = next;
        return this;
    }

    public Instance addEffect(EffectInstance effect) {
        this.effects.add(effect);
        return this;
    }

    public Pattern getPattern() {
        return this.pattern;
    }

    public TargetType getTargetType() {
        return this.targetType;
    }

    /**
     * List of all known attribute values. Keys found in {@link Attributes}.
     * Use {@link Map#getOrDefault(Object, Object)} to retrieve values, as
     * only attributes with values and ranges defined in Module yamls will
     * appear in here.
     * @see #getAttributeValue(String)
     */
    public Map<String, Double> getAttributeValues() {
        return this.attributeValues;
    }

    /**
     * Retrieve the value for a given {@link Attributes}. Defaults to 1 if neither
     * value nor range were defined in the Module's yaml
     */
    public double getAttributeValue(String attribute) {
        return this.attributeValues.getOrDefault(attribute, 1.0);
    }

    public double getManaCost() {
        return this.manaCost;
    }

    public double getBurnoutCost() {
        return this.burnoutCost;
    }

    public Interactor getCaster() {
        return this.caster;
    }

    public static Instance fromNBT(World world, NbtCompound nbt) {
        Pattern pattern;
        TargetType targetType;
        Map<String, Double> attributeValues = null;
        double manaCost = 0;
        double burnoutCost = 0;

        ShapeInstance nextShape = null;
        List<EffectInstance> effects = new LinkedList<>();

        Interactor caster;
        NbtCompound extraData = new NbtCompound();

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
        return null;
    }

    public NbtCompound getExtraData() {
        return extraData;
    }

    public void run(World world, Interactor target) {
        this.pattern.run(world, this, target);
    }

    @Environment(EnvType.CLIENT)
    public void runClient(World world, Interactor target) {
        this.pattern.runClient(world, this, target);
    }

    public List<Color[]> getEffectColors() {

        List<Color[]> colors = this.effects.stream()
                .map(EffectInstance::getPattern)
                .map(Pattern::getColors).collect(Collectors.toList());
        if (nextShape != null) {
            colors.addAll(nextShape.getEffectColors());
        }
        return colors;
    }

    public NbtCompound toNBT() {
        NbtCompound nbt = new NbtCompound();

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

        return nbt;
    }
}
