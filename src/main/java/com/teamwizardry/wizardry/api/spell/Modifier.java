package com.teamwizardry.wizardry.api.spell;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.Item;

public class Modifier implements ISpellComponent
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
    
    @Override public String getName() { return name; }
    
    @Override public Item getItem() { return item; }
    
    public List<AttributeModifier> getAttributeModifiers(String attribute)
    {
        return attributeModifiers.getOrDefault(attribute, new LinkedList<>());
    }
    
    public Set<String> getAffectedAttributes()
    {
        return attributeModifiers.keySet();
    }
}
