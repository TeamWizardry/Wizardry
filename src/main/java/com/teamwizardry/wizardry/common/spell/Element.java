package com.teamwizardry.wizardry.common.spell;

import com.teamwizardry.wizardry.api.spell.ISpellComponent;

import net.minecraft.item.Item;

public class Element implements ISpellComponent
{
    private final String name;
    private final Item item;

    public Element(String name, Item item)
    {
        this.name = name;
        this.item = item;
    }
    
    @Override public String getName() { return this.name; }
    @Override public Item getItem() { return this.item; }
}
