package com.teamwizardry.wizardry.api.spell.attribute;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;

import java.util.List;

@FunctionalInterface
public interface ModifierPredicate {
	List<AttributeModifier> apply(SpellRing spell, SpellData data);
}