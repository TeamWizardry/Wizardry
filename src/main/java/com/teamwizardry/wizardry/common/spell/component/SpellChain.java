package com.teamwizardry.wizardry.common.spell.component;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.teamwizardry.librarianlib.core.util.kotlin.InconceivableException;
import com.teamwizardry.librarianlib.scribe.Save;

import net.minecraft.nbt.NbtCompound;

public abstract class SpellChain// implements INBTSerializable<NbtCompound>
{
    public static final String MODULE = "module";
    public static final String TARGET = "target";
    public static final String MODIFIERS = "modifiers";
    public static final String MULTIPLIER = "multiplier";
    public static final String NEXT = "next";
    public static final String EFFECTS = "effects";
    
    @Save protected Module module;
    @Save protected TargetType targetType = TargetType.ALL;
    @Save protected Map<String, Integer> modifiers;
    @Save protected double manaMultiplier;
    
    public SpellChain(Module module)
    {
        this.module = module;
        this.modifiers = new HashMap<>();
    }
    
    public SpellChain addModifier(Modifier modifier)
    {
        String attribute = modifier.getAttribute();
        modifiers.merge(attribute, 1, (a,b) -> a+b);
        manaMultiplier *= module.getCostPerModifier(attribute);
        return this;
    }
    
    public SpellChain setTarget(TargetType target) { this.targetType = target; return this; }
    
    public Instance toInstance(Interactor caster)
    {
        // TODO: Get modifications from Caster (Halo, potions, autocaster tiers, etc.)
        
        Map<String, Double> attributeValues = new HashMap<>();
        // Set the value for all unmodified values
        module.getAllAttributes().forEach(attribute -> attributeValues.put(attribute, module.getAttributeValue(attribute, 0)));
        // Then set the modified ones with their proper totals
        modifiers.forEach((attribute, count) -> attributeValues.put(attribute, module.getAttributeValue(attribute, count)));
        
        if (module instanceof ModuleShape)
            return new ShapeInstance(module.getPattern(), targetType, attributeValues, module.getBaseManaCost() * manaMultiplier, module.getBaseBurnoutCost() * manaMultiplier, caster);
        else if (module instanceof ModuleEffect)
            return new EffectInstance(module.getPattern(), targetType, attributeValues, module.getBaseManaCost() * manaMultiplier, module.getBaseBurnoutCost() * manaMultiplier, caster);
        throw new InconceivableException("How? There are only two module types, you shouldn't ever be constructing the root");
    }
    
    public NbtCompound serializeNBT()
    {
        NbtCompound nbt = new NbtCompound();
        
        String moduleName = ComponentRegistry.getModules().entrySet().stream().filter(entry -> entry.getValue().equals(module)).map(Entry::getKey).findFirst().get();
        nbt.putString(MODULE, moduleName);
        
        String targetVal = targetType.name();
        nbt.putString(TARGET, targetVal);
        
        NbtCompound modifiers = new NbtCompound();
        this.modifiers.forEach(modifiers::putInt);
        nbt.put(MODIFIERS, modifiers);
        
        nbt.putDouble(MULTIPLIER, manaMultiplier);
        
        return nbt;
    }

    public void deserializeNBT(NbtCompound nbt)
    {
        this.module = ComponentRegistry.getModules().get(nbt.getString(MODULE));
        this.targetType = TargetType.valueOf(nbt.getString(TARGET));
        
        NbtCompound modifiers = nbt.getCompound(MODIFIERS);
        modifiers.getKeys().forEach(attribute -> this.modifiers.put(attribute, modifiers.getInt(attribute)));
        
        this.manaMultiplier = nbt.getDouble(MULTIPLIER);
    }
}
