package com.teamwizardry.wizardry.common.spell.component;

import java.util.List;
import java.util.Map;

import com.teamwizardry.wizardry.api.spell.PatternShape;

import net.minecraft.item.Item;

public class ModuleShape extends Module
{
    protected final String form;
    
    public ModuleShape(PatternShape pattern, String name, List<Item> items, double baseManaCost, double baseBurnoutCost, String form, String element, Map<String, Double> modifierCosts, Map<String, List<Double>> attributeValues)
    {
        super(pattern, name, items, baseManaCost, baseBurnoutCost, element, modifierCosts, attributeValues);
        this.form = form;
    }
    
    @Override public PatternShape getPattern() { return (PatternShape) pattern; }
    
    public String getForm() { return form; }
}
