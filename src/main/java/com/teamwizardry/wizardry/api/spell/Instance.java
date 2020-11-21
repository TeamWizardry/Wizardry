package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.librarianlib.core.util.kotlin.InconceivableException;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.network.CRenderSpellPacket;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.network.PacketDistributor;

import java.awt.Color;
import java.util.*;
import java.util.stream.Collectors;

import static com.teamwizardry.wizardry.api.StringConsts.*;

public abstract class Instance {
    protected Pattern pattern;
    protected TargetType targetType;
    protected Map<String, Double> attributeValues;
    protected double manaCost;
    protected double burnoutCost;

    protected ShapeInstance nextShape;
    protected List<EffectInstance> effects;

    protected Interactor caster;
    protected CompoundNBT extraData;

    public Instance(Pattern pattern, TargetType targetType, Map<String, Double> attributeValues, double manaCost,
                    double burnoutCost, Interactor caster) {
        this.pattern = pattern;
        this.targetType = targetType;
        this.attributeValues = attributeValues;
        this.manaCost = manaCost;
        this.burnoutCost = burnoutCost;

        this.caster = caster;
        this.extraData = new CompoundNBT();

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

    @OnlyIn(Dist.CLIENT)
    public void runClient(World world, Interactor target) {
        this.pattern.runClient(world, this, target);
    }

    public Pattern getPattern() {
        return this.pattern;
    }

    public TargetType getTargetType() {
        return this.targetType;
    }

    public Map<String, Double> getAttributeValues() {
        return this.attributeValues;
    }

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

    public void run(World world, Interactor target) {
        this.pattern.run(world, this, target);

        Wizardry.NETWORK.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(target.getPos().x,
                        target.getPos().y,
                        target.getPos().z,
                        256,
                        world.getDimension().getType())),
                new CRenderSpellPacket.Packet(toNBT(), target.toNBT()));
    }
    
    public List<Color[]> getEffectColors()
    {
        return this.effects.stream().map(EffectInstance::getPattern).map(Pattern::getColors).collect(Collectors.toList());
    }

    public CompoundNBT toNBT() {
        CompoundNBT nbt = new CompoundNBT();

        if (pattern != null)
            nbt.putString(PATTERN, pattern.getRegistryName().toString());

        if (targetType != null)
            nbt.putString(TARGET_TYPE, targetType.toString());

        if (attributeValues != null) {
            CompoundNBT nbtAttributeValues = new CompoundNBT();
            attributeValues.forEach((attribute, value) -> nbtAttributeValues.putDouble(attribute, value));
            nbt.put(ATTRIBUTE_VALUES, nbtAttributeValues);
        }

        nbt.putDouble(MANA_COST, manaCost);
        nbt.putDouble(BURNOUT_COST, burnoutCost);

        if (nextShape != null)
            nbt.put(NEXT_SHAPE, nextShape.toNBT());

        if (!effects.isEmpty()) {
            CompoundNBT nbtEffects = new CompoundNBT();
            for (EffectInstance instance : effects)
                nbtEffects.put(effects.indexOf(instance) + "", instance.toNBT());
            nbt.put(EFFECTS, nbtEffects);
        }
        
        nbt.put(CASTER, caster.toNBT());
        nbt.put(EXTRA_DATA, extraData);
        
        return nbt;
    }
    
    public static Instance fromNBT(World world, CompoundNBT nbt) {
        Pattern pattern = null;
        TargetType targetType = null;
        Map<String, Double> attributeValues = null;
        double manaCost = 0;
        double burnoutCost = 0;

        ShapeInstance nextShape = null;
        List<EffectInstance> effects = null;

        Interactor caster;
        CompoundNBT extraData = null;

        if (nbt.contains(PATTERN))
            pattern = GameRegistry.findRegistry(Pattern.class).getValue(new ResourceLocation(nbt.getString(PATTERN)));
        else throw new InconceivableException("Pattern must always be specified.");

        if (nbt.contains(TARGET_TYPE))
            targetType = TargetType.valueOf(nbt.getString(TARGET_TYPE));
        else throw new InconceivableException("TargetType enum must always be specified.");

        if (nbt.contains(ATTRIBUTE_VALUES)) {
            CompoundNBT nbtAttributeValues = nbt.getCompound(ATTRIBUTE_VALUES);
            attributeValues = new HashMap<>();
            for (String key : nbtAttributeValues.keySet())
                attributeValues.put(key, nbtAttributeValues.getDouble(key));
        }

        if (nbt.contains(MANA_COST))
            manaCost = nbt.getDouble(MANA_COST);

        if (nbt.contains(BURNOUT_COST))
            burnoutCost = nbt.getDouble(BURNOUT_COST);

        if (nbt.contains(NEXT_SHAPE))
            nextShape = (ShapeInstance) fromNBT(world, nbt.getCompound(NEXT_SHAPE));

        if (nbt.contains(EFFECTS)) {
            CompoundNBT nbtEffects = nbt.getCompound(EFFECTS);
            effects = new ArrayList<>();
            for (int i = 0; i < nbtEffects.size(); i++)
                effects.add((EffectInstance) fromNBT(world, nbtEffects.getCompound(i + "")));
        }
        if (nbt.contains(CASTER))
            caster = Interactor.fromNBT(world, nbt.getCompound(CASTER));
        else throw new InconceivableException("Caster Interactor must always be specified.");

        if (nbt.contains(EXTRA_DATA))
            extraData = nbt.getCompound(EXTRA_DATA);

        Instance instance;
        if (extraData != null && extraData.contains(PATTERN_TYPE)) {
            String patternType = extraData.getString(PATTERN_TYPE);
            if (patternType.equals("shape"))
                instance = new ShapeInstance(pattern, targetType, attributeValues, manaCost, burnoutCost, caster);
            else if (patternType.equals("effect"))
                instance = new EffectInstance(pattern, targetType, attributeValues, manaCost, burnoutCost, caster);
            else throw new InconceivableException("Pattern type must be Shape or Effect");
            instance.nextShape = nextShape;
            instance.effects = effects;
            instance.extraData = extraData;
        }
        else throw new InconceivableException("Pattern type must always be specified.");
        
        return instance;
    }
}
