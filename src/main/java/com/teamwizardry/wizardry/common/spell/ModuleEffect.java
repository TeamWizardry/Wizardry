package com.teamwizardry.wizardry.common.spell;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import com.teamwizardry.wizardry.api.spell.PatternEffect;

import net.minecraft.item.Item;

public class ModuleEffect extends Module
{
    protected final String action;
    protected final Color primaryColor;
    protected final Color secondaryColor;
    
    public ModuleEffect(PatternEffect pattern, String name, List<Item> items, Color primary, Color secondary, String action, String element, Map<String, Double> modifierCosts)
    {
        super(pattern, name, items, element, modifierCosts);
        this.action = action;
        this.primaryColor = primary;
        this.secondaryColor = secondary;
    }
    
    public String getAction() { return action; }
    
    public Color getPrimaryColor() { return primaryColor; }

    public Color getSecondaryColor() { return secondaryColor; }
    
    @Override public PatternEffect getPattern() { return (PatternEffect) pattern; }
}
