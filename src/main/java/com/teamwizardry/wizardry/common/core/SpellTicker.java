package com.teamwizardry.wizardry.common.core;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.capability.world.WizardryWorld;
import com.teamwizardry.wizardry.api.capability.world.WizardryWorldCapability;
import com.teamwizardry.wizardry.api.spell.IDelayedModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.module.Module;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashSet;

/**
 * Created by Demoniaque.
 */
@Mod.EventBusSubscriber(modid = Wizardry.MODID)
public class SpellTicker {

	private static HashSet<LingeringObject> lingeringStorageSet = new HashSet<>();
	private static HashSet<DelayedObject> delayedStorageSet = new HashSet<>();

	@SubscribeEvent
	public static void tick(TickEvent.WorldTickEvent event) {
		if (event.world.isRemote) return;
		if (event.phase != TickEvent.Phase.END) return;

		World world = event.world;
		WizardryWorld worldCap = WizardryWorldCapability.get(world);

		worldCap.getLingeringObjects().removeIf(lingeringObject -> {
			long fromWorldTime = lingeringObject.getWorldTime();
			long currentWorldTime = event.world.getTotalWorldTime();
			long subtract = currentWorldTime - fromWorldTime;

			if (lingeringObject.getExpiry() <= subtract) return true;

			lingeringObject.getSpellRing().runSpellRing(lingeringObject.getSpellData().copy());

			return false;
		});

		worldCap.getDelayedObjects().removeIf(delayedObject -> {
			long fromWorldTime = delayedObject.getWorldTime();
			long currentWorldTime = event.world.getTotalWorldTime();
			long subtract = currentWorldTime - fromWorldTime;

			if (delayedObject.getExpiry() > subtract) return false;

			//if (delayedObject.spellData == null) return true;
			((IDelayedModule) delayedObject.getModule()).runDelayedEffect(delayedObject.getSpellData(), delayedObject.getSpellRing());

			return true;
		});
	}

	public static class LingeringObject implements INBTSerializable<NBTTagCompound> {

		private SpellRing spellRing;
		private SpellData spellData;
		private long worldTime;
		private int expiry;

		public LingeringObject(SpellRing spellRing, SpellData spellData, long worldTime, int expiry) {
			this.spellRing = spellRing;
			this.spellData = spellData;
			this.worldTime = worldTime;
			this.expiry = expiry;
		}

		public LingeringObject() {
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

		public static LingeringObject deserialize(NBTTagCompound compound) {
			LingeringObject object = new LingeringObject();
			object.deserializeNBT(compound);
			return object;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound compound = new NBTTagCompound();
			if (spellRing != null)
				compound.setTag("spell_ring", spellRing.serializeNBT());
			if (spellData != null)
				compound.setTag("spell_data", spellData.serializeNBT());
			compound.setLong("world_time", worldTime);
			compound.setInteger("expiry", expiry);
			return null;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			spellRing = SpellRing.deserializeRing(nbt.getCompoundTag("spell_ring"));
			spellData = SpellData.deserializeData(nbt.getCompoundTag("spell_data"));
			worldTime = nbt.getLong("world_time");
			expiry = nbt.getInteger("expiry");
		}
	}

	public static class DelayedObject implements INBTSerializable<NBTTagCompound> {

		private Module module;
		private SpellRing spellRing;
		private SpellData spellData;
		private long worldTime;
		private int expiry;

		public DelayedObject(Module module, SpellRing spellRing, SpellData spellData, long worldTime, int expiry) {
			this.module = module;
			this.spellRing = spellRing;
			this.spellData = spellData;
			this.worldTime = worldTime;
			this.expiry = expiry;
		}

		public DelayedObject() {
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

		public static DelayedObject deserialize(NBTTagCompound compound) {
			DelayedObject object = new DelayedObject();
			object.deserializeNBT(compound);
			return object;
		}

		public Module getModule() {
			return module;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound compound = new NBTTagCompound();
			if (spellRing != null)
				compound.setTag("spell_ring", spellRing.serializeNBT());
			if (spellData != null) compound.setTag("spell_data", spellData.serializeNBT());
			compound.setLong("world_time", worldTime);
			compound.setInteger("expiry", expiry);
			if (module != null)
				compound.setTag("module", module.serialize());
			return compound;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			if (nbt.hasKey("spell_ring"))
				spellRing = SpellRing.deserializeRing(nbt.getCompoundTag("spell_ring"));
			if (nbt.hasKey("spell_data"))
				spellData = SpellData.deserializeData(nbt.getCompoundTag("spell_data"));
			if (nbt.hasKey("world_time"))
				worldTime = nbt.getLong("world_time");
			if (nbt.hasKey("expiry"))
				expiry = nbt.getInteger("expiry");
			if (nbt.hasKey("module"))
				module = Module.deserialize(nbt.getString("module"));
		}
	}
}
