package com.teamwizardry.wizardry.common.spell;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.teamwizardry.wizardry.api.spell.AttributeModifier;
import com.teamwizardry.wizardry.api.spell.ISpellComponent;

import net.minecraft.item.Item;

public class Modifier implements ISpellComponent
{
    private String name;
    private List<Item> items;
    private Map<String, List<AttributeModifier>> attributeModifiers;
    
    public Modifier(String name, List<Item> items, Map<String, List<AttributeModifier>> attributeModifiers)
    {
        this.name = name;
        this.items = items;
        this.attributeModifiers = attributeModifiers;
    }
    
    @Override public String getName() { return name; }
    
    @Override public List<Item> getItems() { return items; }
    
    public List<AttributeModifier> getAttributeModifiers(String attribute)
    {
        return attributeModifiers.getOrDefault(attribute, new LinkedList<>());
    }
    
    public Set<String> getAffectedAttributes()
    {
        return attributeModifiers.keySet();
    }
}
