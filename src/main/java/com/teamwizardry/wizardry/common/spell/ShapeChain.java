package com.teamwizardry.wizardry.common.spell;

import java.util.LinkedList;
import java.util.List;

public class ShapeChain extends SpellChain
{
    private ShapeChain next;
    private List<EffectChain> effects;
    
    public ShapeChain(ModuleShape shape)
    {
        super(shape);
        effects = new LinkedList<>();
    }
    
    public ShapeChain setNext(ShapeChain nextShape) { this.next = nextShape; return this; }
    public ShapeChain addEffect(EffectChain chain) { this.effects.add(chain); return this; }
    
    @Override
    public void run()
    {
        super.run();
        effects.stream().forEach(SpellChain::run);
        next.run();
    }
}
