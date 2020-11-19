package com.teamwizardry.wizardry.common.spell;

import com.teamwizardry.wizardry.api.spell.EffectInstance;
import com.teamwizardry.wizardry.api.spell.Interactor;
import com.teamwizardry.wizardry.common.spell.component.SpellChain;

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
