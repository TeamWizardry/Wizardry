package com.teamwizardry.wizardry.api.spell;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Range;

import net.minecraft.item.Item;

public class ModuleShape extends Module
{
    public ModuleShape(Pattern pattern, String name, Item item, Map<String, Range<Integer>> attributeRanges, List<String> tags, List<String> hiddenTags)
    {
        super(pattern, name, item, attributeRanges, tags, hiddenTags);
    }
}
