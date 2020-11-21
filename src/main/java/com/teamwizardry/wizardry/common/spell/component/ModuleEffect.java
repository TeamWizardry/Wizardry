package com.teamwizardry.wizardry.common.spell.component;

import com.teamwizardry.wizardry.api.spell.PatternEffect;

import net.minecraft.item.Item;

import java.util.List;
import java.util.Map;

public class ModuleEffect extends Module {
    protected final String action;

    public ModuleEffect(PatternEffect pattern, String name, List<Item> items, double baseManaCost, double baseBurnoutCost, String action, String element, Map<String, Double> modifierCosts, Map<String, List<Double>> attributeValues) {
        super(pattern, name, items, baseManaCost, baseBurnoutCost, element, modifierCosts, attributeValues);
        this.action = action;
    }
    
    public String getAction() { return action; }
    
    @Override public PatternEffect getPattern() { return (PatternEffect) pattern; }
}
