package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.wizardry.api.spell.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.spell.attribute.ModifierPredicate;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class SpellModifierRegistry
{
	private static WeakHashMap<UUID, HashMap<ResourceLocation, ModifierPredicate>> entityModifiers = new WeakHashMap<>();

	public static boolean addModifier(Entity entity, ResourceLocation loc, ModifierPredicate predicate) {
		HashMap<ResourceLocation, ModifierPredicate> predicates = entityModifiers.get(entity.getUniqueID());
		if (predicates == null) predicates = new HashMap<>();
		
		predicates.put(loc, predicate);
		entityModifiers.put(entity.getUniqueID(), predicates);
		
		return false;
	}
	
	public static boolean removeModifier(Entity entity, ResourceLocation loc)
	{
		HashMap<ResourceLocation, ModifierPredicate> predicates = entityModifiers.get(entity.getUniqueID());
		if (predicates == null) return false;
		return predicates.remove(loc) != null;
	}
	
	public static List<AttributeModifier> compileModifiers(Entity entity, SpellRing spell, SpellData data)
	{
		List<AttributeModifier> modifiers = new LinkedList<>();
		HashMap<ResourceLocation, ModifierPredicate> predicates = entityModifiers.get(entity.getUniqueID());
		if (predicates == null)
			return modifiers;

		for (ModifierPredicate predicate : predicates.values())
			modifiers.addAll(predicate.apply(spell, data));
		return modifiers;
	}
}
