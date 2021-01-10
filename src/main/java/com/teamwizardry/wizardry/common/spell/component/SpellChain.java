package com.teamwizardry.wizardry.common.spell.component;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.teamwizardry.librarianlib.core.util.kotlin.InconceivableException;
import com.teamwizardry.wizardry.api.spell.EffectInstance;
import com.teamwizardry.wizardry.api.spell.Instance;
import com.teamwizardry.wizardry.api.spell.Interactor;
import com.teamwizardry.wizardry.api.spell.ShapeInstance;
import com.teamwizardry.wizardry.api.spell.TargetType;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class SpellChain implements INBTSerializable<CompoundNBT>
{
    protected static final String MODULE = "module";
    protected static final String TARGET = "target";
    protected static final String MODIFIERS = "modifiers";
    protected static final String MULTIPLIER = "multiplier";
    
    protected Module module;
    protected TargetType targetType = TargetType.ALL;
    protected Map<String, Integer> modifiers;
    protected double manaMultiplier;
    
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
    
    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        
        String moduleName = ComponentRegistry.getModules().entrySet().stream().filter(entry -> entry.getValue().equals(module)).map(Entry::getKey).findFirst().get();
        nbt.putString(MODULE, moduleName);
        
        String targetVal = targetType.name();
        nbt.putString(TARGET, targetVal);
        
        CompoundNBT modifiers = new CompoundNBT();
        this.modifiers.forEach(modifiers::putInt);
        nbt.put(MODIFIERS, modifiers);
        
        nbt.putDouble(MULTIPLIER, manaMultiplier);
        
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.module = ComponentRegistry.getModules().get(nbt.getString(MODULE));
        this.targetType = TargetType.valueOf(nbt.getString(TARGET));
        
        CompoundNBT modifiers = nbt.getCompound(MODIFIERS);
        modifiers.keySet().forEach(attribute -> this.modifiers.put(attribute, modifiers.getInt(attribute)));
        
        this.manaMultiplier = nbt.getDouble(MULTIPLIER);
    }
}
