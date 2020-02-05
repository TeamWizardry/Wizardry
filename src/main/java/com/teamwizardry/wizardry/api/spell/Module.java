package com.teamwizardry.wizardry.api.spell;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Range;

import net.minecraft.item.Item;

public class Module
{
    // Identifying data - must be unique
    private final Pattern pattern;
    private final String name;
    private final Item item;

    // Variable data
    private final Color primaryColor;
    private final Color secondaryColor;
    private final Map<String, Range<Integer>> attributeRanges;

    // Modifier and Usage Metadata
    private final List<String> tags;
    private final List<String> hiddenTags;

    public Module(Pattern pattern, String name, Item item, Color primary, Color secondary, Map<String, Range<Integer>> attributeRanges, List<String> tags, List<String> hiddenTags)
    {
        this.pattern = pattern;
        this.name = name;
        this.item = item;
        this.primaryColor = primary;
        this.secondaryColor = secondary;
        this.attributeRanges = attributeRanges;
        this.tags = tags;
        this.hiddenTags = hiddenTags;
    }

    public Pattern getPattern()
    { return pattern; }

    public String getName()
    { return name; }

    public Item getItem()
    { return item; }

    public Color getPrimaryColor()
    { return primaryColor; }

    public Color getSecondaryColor()
    { return secondaryColor; }

    public Map<String, Range<Integer>> getAttributeRanges()
    { return attributeRanges; }

    public List<String> getTags()
    { return tags; }

    public List<String> getHiddenTags()
    { return hiddenTags; }

    public String toString()
    {
        return PatternRegistry.getName(pattern) + ":" + name + " = [" + item + ", " + primaryColor + ", " + secondaryColor + ", " + attributeRanges + ", " + tags + ", " + hiddenTags + "]";
    }
}
