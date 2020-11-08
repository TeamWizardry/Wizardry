package com.teamwizardry.wizardry.common.spell.component;

import java.util.List;

import com.teamwizardry.wizardry.api.spell.ISpellComponent;

import net.minecraft.item.Item;

public class Modifier implements ISpellComponent
{
    private String name;
    private List<Item> items;
    
    public Modifier(String name, List<Item> items)
    {
        this.name = name;
        this.items = items;
    }
    
    @Override public String getName() { return name; }
    
    @Override public List<Item> getItems() { return items; }
    
    public String getAttribute()
    {
        return name;
    }
}
