package com.teamwizardry.wizardry.api.capability.spell;

import com.teamwizardry.wizardry.common.spell.component.ShapeChain;

public interface ISpellCapability
{
    ShapeChain getSpell();
    
    void setSpell(ShapeChain spell);
}
