package com.teamwizardry.wizardry.api.spell;

import java.util.HashMap;
import java.util.Map;

public abstract class PatternEffect extends Pattern
{
    private Map<Class<PatternShape>, ISpellComponent> overrides = new HashMap<>(); 
    
    public boolean overrides(PatternShape shape) { return overrides.containsKey(shape.getClass()); }
    
    public ISpellComponent getOverride(PatternShape shape) { return overrides.get(shape.getClass()); }
}
