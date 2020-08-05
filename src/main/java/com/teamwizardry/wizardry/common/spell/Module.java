package com.teamwizardry.wizardry.common.spell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.ISpellComponent;
import com.teamwizardry.wizardry.api.spell.Pattern;

import net.minecraft.item.Item;

public class Module implements ISpellComponent
{
    // Identifying data - must be unique
    protected final Pattern pattern;
    protected final String name;
    protected final Item item;

    // Variable data
    private Map<String, Module> elementalVariants = new HashMap<>();

    // Modifier and Usage Metadata
    protected final List<String> tags;
    protected final List<String> hiddenTags;

    public Module(Pattern pattern, String name, Item item, List<String> tags, List<String> hiddenTags)
    {
        this.pattern = pattern;
        this.name = name;
        this.item = item;
        this.tags = tags;
        this.hiddenTags = hiddenTags;
    }

    public Pattern getPattern()
    { return pattern; }

    @Override public String getName()
    { return name; }

    @Override public Item getItem()
    { return item; }

    public Module getElementalVariant(String element)
    { return elementalVariants.getOrDefault(element, this); }
    
    public Module addElementalVariant(String element, Module variant)
    {
        if (elementalVariants.containsKey(element))
        {
            Wizardry.LOGGER.warn("Module elemental variant registration failed for element " + element + ", already mapped to " + elementalVariants.get(element));
            return this;
        }
        
        // There's a better way to check if two objects are instances of the same subclass of Module, right?
        Class<?> a = this.getClass();
        Class<?> b = variant.getClass();
        while (!a.isAssignableFrom(b))
            a = a.getSuperclass();
        if (Module.class.isAssignableFrom(a))
        {
            Wizardry.LOGGER.warn("Attempting illegal elemental variant registration, common superclass is " + a.getCanonicalName() + ", but must be a subclass of " + Module.class.getCanonicalName());
            return this;
        }
        
        elementalVariants.put(element, variant);
        return this;
    }
    
    public List<String> getTags()
    { return tags; }

    public List<String> getHiddenTags()
    { return hiddenTags; }
    
    public String toString()
    {
        return pattern.getRegistryName() + ":" + name + " = [" + item + ", " + tags + ", " + hiddenTags + "]";
    }
}
