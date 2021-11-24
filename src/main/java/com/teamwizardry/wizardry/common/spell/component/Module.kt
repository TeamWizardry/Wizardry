package com.teamwizardry.wizardry.common.spell.component;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.minecraft.item.Item;

public class Module implements ISpellComponent
{
    // Identifying data - must be unique
    protected final Pattern pattern;
    protected final String name;
    protected final List<Item> items;

    // Base Costs
    protected final double baseManaCost;
    protected final double baseBurnoutCost;
    
    // Modifier and Usage Metadata
    protected final String element;
    protected final Map<String, Double> modifierCosts;
    protected final Map<String, List<Double>> attributeValues;

    public Module(Pattern pattern, String name, List<Item> items, double baseManaCost, double baseBurnoutCost, String element, Map<String, Double> modifierCosts, Map<String, List<Double>> attributeValues)
    {
        this.pattern = pattern;
        this.name = name;
        this.items = items;
        this.baseManaCost = baseManaCost;
        this.baseBurnoutCost = baseBurnoutCost;
        this.element = element;
        this.modifierCosts = modifierCosts;
        this.attributeValues = attributeValues;
    }

    public Pattern getPattern() { return pattern; }

    @Override public String getName() { return name; }

    @Override public List<Item> getItems() { return items; }

    public double getBaseManaCost() { return baseManaCost; }
    
    public double getBaseBurnoutCost() { return baseBurnoutCost; }
    
    public String getElement() { return element; }
    
    public double getCostPerModifier(String modifier) { return modifierCosts.getOrDefault(modifier, 0.05); }
    
    public double getAttributeValue(String attribute, int count)
    {
        List<Double> values = attributeValues.getOrDefault(attribute, Collections.singletonList(1d));
        if (count < 0) count = 0;
        if (count >= values.size()) count = values.size()-1;
        return values.get(count);
    }
    
    /**
     * All attributes used by this module (or that at least have a non-default value) 
     */
    public List<String> getAllAttributes()
    {
        return new LinkedList<>(attributeValues.keySet());
    }
    
    /**
     * All attributes that will use modifiers. Attributes with just a single value are
     * non-default, but unmodifiable, and as such should not be available for having modifiers
     */
    public List<String> getAttributes()
    {
        return attributeValues.keySet().stream().filter(attribute -> attributeValues.get(attribute).size() > 1).collect(Collectors.toList());
    }

    public Map<String, List<Double>> getAttributeValues() {
        return attributeValues;
    }

//    public String toString() { return pattern.getRegistryName() + ":" + name + " = [" + items + ", " + element + "]"; }
    
//    public String getTranslationKey() { return "wizardry.spell." + pattern.getRegistryName() + ":" + name; }
    
//    public String getTranslationKey(String key) { return getTranslationKey() + "." + key; }
}
