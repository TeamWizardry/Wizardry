package com.teamwizardry.wizardry.common.spell;

import java.util.List;
import java.util.Set;

import com.teamwizardry.wizardry.api.spell.ISpellComponent;

import net.minecraft.item.Item;

public class Modifier implements ISpellComponent
{
    private String name;
    private List<Item> items;
    private Set<String> attributes;
    
    public Modifier(String name, List<Item> items, Set<String> attributes)
    {
        this.name = name;
        this.items = items;
        this.attributes = attributes;
    }
    
    @Override public String getName() { return name; }
    
    @Override public List<Item> getItems() { return items; }
    
    public Set<String> getAffectedAttributes()
    {
        return attributes;
    }
}
