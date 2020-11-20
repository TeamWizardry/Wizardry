package com.teamwizardry.wizardry.common.spell;

import java.util.LinkedList;
import java.util.List;

import com.teamwizardry.wizardry.api.spell.Interactor;
import com.teamwizardry.wizardry.api.spell.ShapeInstance;
import com.teamwizardry.wizardry.common.spell.component.SpellChain;

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
    public ShapeInstance toInstance(Interactor caster)
    {
        ShapeInstance instance = (ShapeInstance) super.toInstance(caster);
        if (next != null)
            instance.setNext(next.toInstance(caster));
        effects.stream().map(effect -> effect.toInstance(caster)).forEach(instance::addEffect);
        return instance;
    }
}
