package com.teamwizardry.wizardry.common.spell;

import java.util.List;
import java.util.Map;

import com.teamwizardry.wizardry.api.spell.AttributeModifier;

public class EffectChain extends SpellChain
{
    public EffectChain(ModuleEffect effect, Map<String, List<AttributeModifier>> modifiers)
    {
        super(effect, modifiers);
    }
}
