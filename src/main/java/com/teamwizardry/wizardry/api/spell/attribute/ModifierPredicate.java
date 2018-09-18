package com.teamwizardry.wizardry.api.spell.attribute;

import java.util.List;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;

@FunctionalInterface
public interface ModifierPredicate<U, V>
{
	public List<AttributeModifier> apply(SpellRing spell, SpellData data);
}