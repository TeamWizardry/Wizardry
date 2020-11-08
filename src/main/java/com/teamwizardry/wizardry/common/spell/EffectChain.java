package com.teamwizardry.wizardry.common.spell;

import com.teamwizardry.wizardry.common.spell.component.SpellChain;

public class EffectChain extends SpellChain
{
    public EffectChain(ModuleEffect effect)
    {
        super(effect);
    }
    
    @Override
    public EffectInstance toInstance()
    {
        return (EffectInstance) super.toInstance();
    }
}
