package com.teamwizardry.wizardry.api.capability.spell;

import com.teamwizardry.librarianlib.foundation.capability.BaseCapability;
import com.teamwizardry.librarianlib.prism.Save;
import com.teamwizardry.wizardry.common.spell.component.ShapeChain;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class SpellCapability extends BaseCapability implements ISpellCapability
{
    @CapabilityInject(ISpellCapability.class)
    public static Capability<ISpellCapability> SPELL_CAPABILITY;
    
    @Save private ShapeChain spell;
    
    public SpellCapability(ShapeChain spell) { this.spell = spell; }
    
    @Override public ShapeChain getSpell() { return this.spell; }
    @Override public void setSpell(ShapeChain spell) { this.spell = spell; }
}
