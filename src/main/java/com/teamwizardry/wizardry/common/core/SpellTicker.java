package com.teamwizardry.wizardry.common.core;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.capability.world.WizardryWorld;
import com.teamwizardry.wizardry.api.capability.world.WizardryWorldCapability;
import com.teamwizardry.wizardry.api.spell.IDelayedModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.common.network.PacketSyncWizardryWorld;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Demoniaque.
 */
@Mod.EventBusSubscriber(modid = Wizardry.MODID)
public class SpellTicker {

	@SubscribeEvent
	public static void tick(TickEvent.WorldTickEvent event) {
		if (event.world.isRemote) return;
		if (event.phase != TickEvent.Phase.END) return;

		World world = event.world;
		WizardryWorld worldCap = WizardryWorldCapability.get(world);

		Set<LingeringObject> tmp1 = new HashSet<>(worldCap.getLingeringObjects());
		Set<DelayedObject> tmp2 = new HashSet<>(worldCap.getDelayedObjects());

		boolean change = false;

		for (LingeringObject lingeringObject : tmp1) {
			long fromWorldTime = lingeringObject.getWorldTime();
			long currentWorldTime = event.world.getTotalWorldTime();
			long subtract = currentWorldTime - fromWorldTime;

			if (subtract > lingeringObject.getExpiry()) {
				worldCap.getLingeringObjects().remove(lingeringObject);
				change = true;
				continue;
			}

			lingeringObject.getSpellRing().runSpellRing(lingeringObject.getSpellData().copy());

		}

		for (DelayedObject delayedObject : tmp2) {
			long fromWorldTime = delayedObject.getWorldTime();
			long currentWorldTime = event.world.getTotalWorldTime();
			long subtract = currentWorldTime - fromWorldTime;

			if (subtract > delayedObject.getExpiry()) {
				worldCap.getDelayedObjects().remove(delayedObject);
				change = true;
				continue;
			}

			((IDelayedModule) delayedObject.getModule()).runDelayedEffect(delayedObject.getSpellData(), delayedObject.getSpellRing());
		}

		if (change) {
			PacketHandler.NETWORK.sendToDimension(new PacketSyncWizardryWorld(worldCap.serializeNBT()), world.provider.getDimension());
		}
	}

	public static class LingeringObject implements INBTSerializable<NBTTagCompound> {

		private SpellRing spellRing;
		private SpellData spellData;
		private long worldTime;
		private int expiry;

		@NotNull
		private final World world;

		public LingeringObject(SpellRing spellRing, SpellData spellData, long worldTime, int expiry) {
			this.spellRing = spellRing;
			this.spellData = spellData;
			this.worldTime = worldTime;
			this.expiry = expiry;
			this.world = spellData.world;
		}

		LingeringObject(@NotNull World world) {
			this.world = world;
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

		public static LingeringObject deserialize(World world, NBTTagCompound compound) {
			LingeringObject object = new LingeringObject(world);
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
			compound.setInteger("world", world.provider.getDimension());
			return null;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			spellRing = SpellRing.deserializeRing(nbt.getCompoundTag("spell_ring"));
			spellData = SpellData.deserializeData(world, nbt.getCompoundTag("spell_data"));
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

		@NotNull
		private final World world;

		public DelayedObject(Module module, SpellRing spellRing, SpellData spellData, long worldTime, int expiry) {
			this.module = module;
			this.spellRing = spellRing;
			this.spellData = spellData;
			this.worldTime = worldTime;
			this.expiry = expiry;
			this.world = spellData.world;
		}

		private DelayedObject(@NotNull World world) {
			this.world = world;
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

		public static DelayedObject deserialize(World world, NBTTagCompound compound) {
			DelayedObject object = new DelayedObject(world);
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
				spellData = SpellData.deserializeData(world, nbt.getCompoundTag("spell_data"));
			if (nbt.hasKey("world_time"))
				worldTime = nbt.getLong("world_time");
			if (nbt.hasKey("expiry"))
				expiry = nbt.getInteger("expiry");
			if (nbt.hasKey("module"))
				module = Module.deserialize(nbt.getString("module"));
		}
	}
}
