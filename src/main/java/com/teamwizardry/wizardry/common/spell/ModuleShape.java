package com.teamwizardry.wizardry.common.spell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.teamwizardry.wizardry.api.spell.PatternShape;

import net.minecraft.item.Item;

public class ModuleShape extends Module
{
    private Map<ModuleEffect, ModuleShape> overrides = new HashMap<>();
    
    public ModuleShape(PatternShape pattern, String name, Item item, List<String> tags, List<String> hiddenTags)
    {
        super(pattern, name, item, tags, hiddenTags);
    }
    
    @Override public PatternShape getPattern() { return (PatternShape) pattern; }
    
    public boolean isOverriddenBy(ModuleEffect effect) { return overrides.containsKey(effect); }
    
    public ModuleShape getOverride(ModuleEffect effect) { return overrides.get(effect); }
}
