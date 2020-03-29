package com.teamwizardry.wizardry.api.spell;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.item.Item;

public class Modifier
{
    private String name;
    private Item item;
    private Map<String, List<AttributeModifier>> attributeModifiers;
    
    public Modifier(String name, Item item, Map<String, List<AttributeModifier>> attributeModifiers)
    {
        this.name = name;
        this.item = item;
        this.attributeModifiers = attributeModifiers;
    }
    
    public String getName() { return name; }
    
    public Item getItem() { return item; }
    
    public List<AttributeModifier> getAttributeModifiers(String attribute)
    {
        return attributeModifiers.getOrDefault(attribute, new LinkedList<>());
    }
}
