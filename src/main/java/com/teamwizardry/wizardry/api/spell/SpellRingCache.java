package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;

/**
 * A singleton to cache all spell rings as resolved from a given NBT or created by the spell builder.
 *
 * @author Avatair
 */
public class SpellRingCache {
	private final HashMap<NBTTagCompound, SpellRing> cache = new HashMap<>();

	public static final SpellRingCache INSTANCE = new SpellRingCache();

	private SpellRingCache() {
	}

	/**
	 * Returns a spell chain from an NBT.
	 *
	 * @param key a compound containing the NBT.
	 * @return the head element of the spell chain.
	 */
	public SpellRing getSpellRingByNBT(NBTTagCompound key) {
		return internalGetSpellRingByNBT(key, true);
	}

	/**
	 * Returns a cached spell chain. Creates a new element in case of cache miss.
	 * <b>NOTE</b>: Called by {@link SpellBuilder} as well.
	 *
	 * @param key       nbt key to retrieve a spell chain from.
	 * @param mayCreate creates a new cache record iff <code>true</code>.
	 * @return a spell chain or <code>null</code> if no spell chain was created.
	 */
	synchronized SpellRing internalGetSpellRingByNBT(NBTTagCompound key, boolean mayCreate) {
		SpellRing spellRing = cache.get(key);
		if (spellRing == null && mayCreate) {
			// deserialize ring and register it
			spellRing = SpellRing.deserializeRing(key);
			spellRing = registerSpellRing(spellRing);
		}

		return spellRing;
	}

	/**
	 * Attempts to registers a spell chain element.
	 * <b>NOTE</b>: Doesn't overwrite an existing element. Returns the actually registered element, matching same NBT key.
	 *
	 * @param spellRing the head element of the spell chain.
	 * @return the cached spell chain element. Is different to passed <code>spellRing</code> in case a matching chain exists in cache.
	 */
	synchronized SpellRing registerSpellRing(SpellRing spellRing) {
		// NOTE: Is a workaround! In the future a spell dictionary is needed.
		if (spellRing.getParentRing() != null)
			throw new IllegalArgumentException("Expects a spell ring chain head.");

		NBTTagCompound nbt = spellRing.serializeNBT();
		SpellRing other = cache.put(nbt, spellRing);
		if (other != null && other != spellRing) {
			cache.put(nbt, other);
			return other;
		} else {
			if (other == null)
				Wizardry.logger.info("SpellRing cache miss for " + spellRing);
			return spellRing;
		}
	}

	/**
	 * Clears the cache.
	 */
	public synchronized void clear() {
		cache.clear();
	}
}
