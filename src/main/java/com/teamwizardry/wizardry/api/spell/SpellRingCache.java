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
	synchronized SpellRing internalGetSpellRingByNBT(NBTTagCompound key, boolean mayCreate) {
		SpellRing spellRing = cache.get(key);
		if( spellRing == null && mayCreate ) {
			// deserialize ring and register it
			spellRing = SpellRing.deserializeRing(key);
			spellRing = registerSpellRing(spellRing);
		}
		
		return spellRing;
	}
	
	synchronized SpellRing registerSpellRing(SpellRing spellRing) {
		// NOTE: Is a workaround!
		if( spellRing.getParentRing() != null )
			throw new IllegalArgumentException("Expects a spell ring chain head.");
		
		NBTTagCompound nbt = spellRing.serializeNBT();
		SpellRing other = cache.put(nbt, spellRing);
		if( other != null && other != spellRing ) {
			cache.put(nbt, other);
			return other;
		}
		else {
			if( other == null )
				Wizardry.logger.info("SpellRing cache miss for " + spellRing);
			return spellRing;
		}
	}

	public synchronized void clear() {
		cache.clear();
	}
}
