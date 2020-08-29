package com.teamwizardry.wizardry.common.spell;

import java.util.List;
import java.util.Map;

import com.teamwizardry.wizardry.api.spell.ISpellComponent;

import net.minecraft.item.Item;

public class Modifier implements ISpellComponent
{
    private String name;
    private List<Item> items;
    private Map<String, Integer> attributeModifiers;
    
    public Modifier(String name, List<Item> items, Map<String, Integer> attributeModifiers)
    {
        this.name = name;
        this.items = items;
        this.attributeModifiers = attributeModifiers;
    }
    
    @Override public String getName() { return name; }
    
    @Override public List<Item> getItems() { return items; }
    
    public Map<String, Integer> getAttributeModifiers()
    {
        return attributeModifiers;
    }
}
