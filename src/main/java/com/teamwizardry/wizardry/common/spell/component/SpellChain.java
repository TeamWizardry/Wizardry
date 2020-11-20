package com.teamwizardry.wizardry.common.spell.component;

import java.util.HashMap;
import java.util.Map;

import com.teamwizardry.librarianlib.core.util.kotlin.InconceivableException;
import com.teamwizardry.wizardry.api.spell.EffectInstance;
import com.teamwizardry.wizardry.api.spell.Instance;
import com.teamwizardry.wizardry.api.spell.Interactor;
import com.teamwizardry.wizardry.api.spell.ShapeInstance;
import com.teamwizardry.wizardry.api.spell.TargetType;
import com.teamwizardry.wizardry.common.spell.ModuleEffect;
import com.teamwizardry.wizardry.common.spell.ModuleShape;

public abstract class SpellChain
{
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
}
