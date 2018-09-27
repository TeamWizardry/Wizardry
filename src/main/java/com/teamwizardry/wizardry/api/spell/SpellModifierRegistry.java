package com.teamwizardry.wizardry.api.spell;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.WeakHashMap;

import com.teamwizardry.wizardry.api.spell.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.spell.attribute.ModifierPredicate;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class SpellModifierRegistry
{
	private static WeakHashMap<UUID, HashMap<ResourceLocation, ModifierPredicate<SpellRing, SpellData>>> entityModifiers = new WeakHashMap<>();
	
	public static boolean addModifier(Entity entity, ResourceLocation loc, ModifierPredicate<SpellRing, SpellData> predicate)
	{
		HashMap<ResourceLocation, ModifierPredicate<SpellRing, SpellData>> predicates = entityModifiers.get(entity.getUniqueID());
		if (predicates == null) predicates = new HashMap<>();
		
		predicates.put(loc, predicate);
		entityModifiers.put(entity.getUniqueID(), predicates);
		
		return false;
	}
	
	public static boolean removeModifier(Entity entity, ResourceLocation loc)
	{
		HashMap<ResourceLocation, ModifierPredicate<SpellRing, SpellData>> predicates = entityModifiers.get(entity.getUniqueID());
		if (predicates == null) return false;
		return predicates.remove(loc) != null;
	}
	
	public static List<AttributeModifier> compileModifiers(Entity entity, SpellRing spell, SpellData data)
	{
		List<AttributeModifier> modifiers = new LinkedList<>();
		HashMap<ResourceLocation, ModifierPredicate<SpellRing, SpellData>> predicates = entityModifiers.get(entity.getUniqueID());
		if (predicates == null)
			return modifiers;
		
		for (ModifierPredicate<SpellRing, SpellData> predicate : predicates.values())
			modifiers.addAll(predicate.apply(spell, data));
		return modifiers;
	}
}
