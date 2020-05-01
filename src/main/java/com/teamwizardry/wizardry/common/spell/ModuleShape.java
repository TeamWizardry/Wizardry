package com.teamwizardry.wizardry.common.spell;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Range;

import com.teamwizardry.wizardry.api.spell.PatternShape;

import net.minecraft.item.Item;

public class ModuleShape extends Module
{
    public ModuleShape(PatternShape pattern, String name, Item item, Map<String, Range<Integer>> attributeRanges, List<String> tags, List<String> hiddenTags)
    {
        super(pattern, name, item, attributeRanges, tags, hiddenTags);
    }
    
    @Override public PatternShape getPattern() { return (PatternShape) pattern; }
}
