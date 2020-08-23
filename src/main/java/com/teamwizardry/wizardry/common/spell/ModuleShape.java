package com.teamwizardry.wizardry.common.spell;

import java.util.List;
import java.util.Map;

import com.teamwizardry.wizardry.api.spell.PatternShape;

import net.minecraft.item.Item;

public class ModuleShape extends Module
{
    protected final String form;
    
    public ModuleShape(PatternShape pattern, String name, List<Item> items, String form, String element, Map<String, Double> modifierCosts)
    {
        super(pattern, name, items, element, modifierCosts);
        this.form = form;
    }
    
    @Override public PatternShape getPattern() { return (PatternShape) pattern; }
    
    public String getForm() { return form; }
}
