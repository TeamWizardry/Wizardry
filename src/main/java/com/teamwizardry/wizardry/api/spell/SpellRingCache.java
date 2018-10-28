package com.teamwizardry.wizardry.api.spell;

import java.util.HashMap;

import com.teamwizardry.wizardry.Wizardry;

import net.minecraft.nbt.NBTTagCompound;

public class SpellRingCache {
	private final HashMap<NBTTagCompound, SpellRing> cache = new HashMap<>();
	
	public static final SpellRingCache INSTANCE = new SpellRingCache();
	
	private SpellRingCache() {
	}
	
	public SpellRing getSpellRingByNBT(NBTTagCompound key) {
		return internalGetSpellRingByNBT(key, true);
	}
	
	/**
	 * <b>NOTE</b>: Called by {@link SpellBuilder} as well. 
	 * 
	 * @param key nbt key to retrieve a spell chain from.
	 * @param mayCreate creates a new cache record iff <code>true</code>.
	 * @return a spell chain or <code>null</code> if no spell chain was created.
	 */
	SpellRing internalGetSpellRingByNBT(NBTTagCompound key, boolean mayCreate) {
		SpellRing spellRing = cache.get(key);
		if( spellRing == null && mayCreate ) {
			// deserialize ring and register it
			spellRing = SpellRing.deserializeRing(key);
			registerSpellRing(spellRing);
			Wizardry.logger.info("SpellRing cache miss for " + spellRing);
		}
		
		return spellRing;
	}
	
	void registerSpellRing(SpellRing spellRing) {
		if( spellRing.getParentRing() != null )
			throw new IllegalArgumentException("Expects a spell ring chain head.");

		cache.put(spellRing.serializeNBT(), spellRing);
		
/*		// Called by SpellBuilder
		SpellRing cur = spellRing;
		while( cur != null ) {
			cache.put(cur.serializeNBT(), cur);
			cur = cur.getChildRing();
		} */
		
	}
}
