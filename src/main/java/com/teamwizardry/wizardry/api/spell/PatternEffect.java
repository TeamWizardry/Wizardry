package com.teamwizardry.wizardry.api.spell;

public abstract class PatternEffect extends Pattern
{
    public boolean overrides(PatternShape shape) { return false; }
}
