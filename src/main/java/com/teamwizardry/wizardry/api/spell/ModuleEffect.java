package com.teamwizardry.wizardry.api.spell;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Range;

import net.minecraft.item.Item;

public class ModuleEffect extends Module
{
    protected final Color primaryColor;
    protected final Color secondaryColor;
    
    public ModuleEffect(Pattern pattern, String name, Item item, Color primary, Color secondary, Map<String, Range<Integer>> attributeRanges, List<String> tags, List<String> hiddenTags)
    {
        super(pattern, name, item, attributeRanges, tags, hiddenTags);
        this.primaryColor = primary;
        this.secondaryColor = secondary;
    }
    
    public Color getPrimaryColor()
    { return primaryColor; }

    public Color getSecondaryColor()
    { return secondaryColor; }
}
