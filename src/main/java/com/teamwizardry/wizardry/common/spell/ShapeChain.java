package com.teamwizardry.wizardry.common.spell;

import java.util.Arrays;

import com.teamwizardry.wizardry.api.spell.Pattern;

public class ShapeChain extends SpellChain
{
    private ShapeChain next;
    private EffectChain[] effects;
    
    public ShapeChain(Pattern pattern, ShapeChain next, EffectChain... effects)
    {
        super(pattern);
        this.next = next;
        this.effects = effects;
    }
    
    @Override
    public void run()
    {
        super.run();
        Arrays.stream(effects).forEach(SpellChain::run);
        next.run();
    }
}
