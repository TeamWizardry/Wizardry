package com.teamwizardry.wizardry.api.spell;

import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;

public class SpellRingCache {
	private final HashMap<NBTTagCompound, SpellRing> cache = new HashMap<>();
	
	public static final SpellRingCache INSTANCE = new SpellRingCache();
	
	private SpellRingCache() {
	}
	
	public SpellRing getSpellRingByNBT(NBTTagCompound key) {
		return internalGetSpellRingByNBT(key, true);
	}
	
	SpellRing internalGetSpellRingByNBT(NBTTagCompound key, boolean mayCreate) {
		SpellRing spellRing = cache.get(key);
		if( spellRing == null && mayCreate ) {
			// deserialize ring and register it
			spellRing = SpellRing.deserializeRing(key);
			registerSpellChain(spellRing);
		}
		
		return spellRing;
	}
	
	void registerSpellChain(SpellRing spellRing) {
		// Called by SpellBuilder
		SpellRing cur = spellRing;
		while( cur != null ) {
			cache.put(cur.serializeNBT(), cur);
			cur = cur.getChildRing();
		}
	}
}
