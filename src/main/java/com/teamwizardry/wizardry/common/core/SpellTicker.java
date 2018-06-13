package com.teamwizardry.wizardry.common.core;

import com.teamwizardry.wizardry.api.spell.IDelayedModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashSet;

/**
 * Created by Demoniaque.
 */
public class SpellTicker {

	private static HashSet<LingeringObject> lingeringStorageMap = new HashSet<>();
	private static HashSet<DelayedObject> delayedStorageMap = new HashSet<>();

	@SubscribeEvent
	public static void tick(TickEvent.WorldTickEvent event) {
		if (event.world.isRemote) return;
		if (event.phase != TickEvent.Phase.END) return;

		lingeringStorageMap.removeIf(lingeringObject -> {
			long fromWorldTime = lingeringObject.getWorldTime();
			long currentWorldTime = event.world.getTotalWorldTime();
			long subtract = currentWorldTime - fromWorldTime;

			if (lingeringObject.getExpiry() <= subtract) return true;

			lingeringObject.getSpellRing().runSpellRing(lingeringObject.getSpellData().copy());

			return false;
		});

		delayedStorageMap.removeIf(delayedObject -> {
			long fromWorldTime = delayedObject.getWorldTime();
			long currentWorldTime = event.world.getTotalWorldTime();
			long subtract = currentWorldTime - fromWorldTime;

			if (delayedObject.getExpiry() > subtract) return false;

			delayedObject.getModule().runDelayedEffect(delayedObject.getSpellData(), delayedObject.getSpellRing());

			return true;
		});
	}

	public static void addLingerSpell(SpellRing spellRing, SpellData data, int expiry) {
		lingeringStorageMap.add(new LingeringObject(spellRing, data, data.world.getTotalWorldTime(), expiry));
	}

	public static void addDelayedSpell(IDelayedModule module, SpellRing spellRing, SpellData data, int expiry) {
		delayedStorageMap.add(new DelayedObject(module, spellRing, data, data.world.getTotalWorldTime(), expiry));
	}

	public static HashSet<LingeringObject> getLingeringStorageMap() {
		return lingeringStorageMap;
	}

	public static HashSet<DelayedObject> getDelayedStorageMap() {
		return delayedStorageMap;
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

	public static class DelayedObject {

		private final IDelayedModule module;
		private final SpellRing spellRing;
		private final SpellData spellData;
		private final long worldTime;
		private final int expiry;

		public DelayedObject(IDelayedModule module, SpellRing spellRing, SpellData spellData, long worldTime, int expiry) {
			this.module = module;
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

		public IDelayedModule getModule() {
			return module;
		}
	}
}
