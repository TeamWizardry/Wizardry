package com.teamwizardry.wizardry.common.spell;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.teamwizardry.wizardry.api.spell.AttributeModifier;

public class ShapeChain extends SpellChain
{
    private ShapeChain next;
    private EffectChain[] effects;
    
    public ShapeChain(ModuleShape shape, Map<String, List<AttributeModifier>> modifiers)
    {
        super(shape, modifiers);
    }
    
    public ShapeChain setNext(ShapeChain nextShape) { this.next = nextShape; return this; }
    public ShapeChain setEffects(EffectChain... chains) { this.effects = chains; return this; }
    
    @Override
    public void run()
    {
        super.run();
        Arrays.stream(effects).forEach(SpellChain::run);
        next.run();
    }
}
