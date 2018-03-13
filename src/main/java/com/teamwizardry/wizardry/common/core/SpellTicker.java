package com.teamwizardry.wizardry.common.core;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashSet;

/**
 * Created by Demoniaque.
 */
public class SpellTicker {

	private static HashSet<LingeringObject> storageMap = new HashSet<>();

	@SubscribeEvent
	public static void tick(TickEvent.WorldTickEvent event) {
		if (event.world.isRemote) return;
		if (event.phase != TickEvent.Phase.END) return;

		storageMap.removeIf(lingeringObject -> {
			long fromWorldTime = lingeringObject.getWorldTime();
			long currentWorldTime = event.world.getTotalWorldTime();
			long subtract = currentWorldTime - fromWorldTime;

			if (lingeringObject.expiry <= subtract) return true;

			lingeringObject.getSpellRing().runSpellRing(lingeringObject.getSpellData().copy());

			return false;
		});
	}

	public static void addLingerSpell(SpellRing spellRing, SpellData data, int expiry) {
		storageMap.add(new LingeringObject(spellRing, data, data.world.getTotalWorldTime(), expiry));
	}

	public static HashSet<LingeringObject> getStorageMap() {
		return storageMap;
	}

	public static class LingeringObject {

		private final SpellRing spellRing;
		private final SpellData spellData;
		private final long worldTime;
		private final int expiry;

		public LingeringObject(SpellRing spellRing, SpellData spellData, long worldTime, int expiry) {
			this.spellRing = spellRing;
			this.spellData = spellData;
			this.worldTime = worldTime;
			this.expiry = expiry;
		}

		public SpellRing getSpellRing() {
			return spellRing;
		}

		public SpellData getSpellData() {
			return spellData;
		}

		public long getWorldTime() {
			return worldTime;
		}

		public int getExpiry() {
			return expiry;
		}
	}
}
