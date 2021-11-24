package com.teamwizardry.wizardry.common.spell.component;

public class EffectChain extends SpellChain
{
    public EffectChain(ModuleEffect effect)
    {
        super(effect);
    }
    
    @Override
    public EffectInstance toInstance(Interactor caster)
    {
        return (EffectInstance) super.toInstance(caster);
    }
}
