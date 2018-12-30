package com.teamwizardry.wizardry.common.core;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.capability.world.WizardryWorld;
import com.teamwizardry.wizardry.api.capability.world.WizardryWorldCapability;
import com.teamwizardry.wizardry.api.spell.IDelayedModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.SpellRingCache;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstance;
import com.teamwizardry.wizardry.common.network.PacketSyncWizardryWorld;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * Created by Demoniaque.
 */
@Mod.EventBusSubscriber(modid = Wizardry.MODID)
public class SpellTicker {

	@SubscribeEvent
	public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.player.world.isRemote) return;

		WizardryWorld cap = WizardryWorldCapability.get(event.player.world);
		if (cap == null) return;

		PacketHandler.NETWORK.sendToDimension(new PacketSyncWizardryWorld(cap.serializeNBT()), event.player.world.provider.getDimension());
	}

	@SubscribeEvent
	public static void tick(TickEvent.WorldTickEvent event) {
		if (event.world.isRemote) return;
		if (event.phase != TickEvent.Phase.END) return;

		World world = event.world;
		WizardryWorld worldCap = WizardryWorldCapability.get(world);

		if (worldCap == null) return;
		if (worldCap.getLingeringObjects() == null) return;

		boolean change = false;

		Iterator<LingeringObject> lingering = worldCap.getLingeringObjects().iterator();
		while (lingering.hasNext()) {
			LingeringObject lingeringObject = lingering.next();
			if (lingeringObject == null) continue;

			long fromWorldTime = lingeringObject.getWorldTime();
			long currentWorldTime = event.world.getTotalWorldTime();
			long subtract = currentWorldTime - fromWorldTime;

			if (subtract > lingeringObject.getExpiry()) {
				lingering.remove();
				change = true;
				continue;
			}

			lingeringObject.getSpellRing().runSpellRing(lingeringObject.getSpellData().copy());
		}

		Iterator<DelayedObject> delayed = worldCap.getDelayedObjects().iterator();
		while (delayed.hasNext()) {
			DelayedObject delayedObject = delayed.next();

			long fromWorldTime = delayedObject.getWorldTime();
			long currentWorldTime = event.world.getTotalWorldTime();
			long subtract = currentWorldTime - fromWorldTime;

			if (subtract > delayedObject.getExpiry()) {
				((IDelayedModule) delayedObject.getModule().getModuleClass()).runDelayedEffect(delayedObject.getSpellData(), delayedObject.getSpellRing());
				delayed.remove();
				change = true;
			}
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
			return compound;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			spellRing = SpellRingCache.INSTANCE.getSpellRingByNBT(nbt.getCompoundTag("spell_ring"));
			spellData = SpellData.deserializeData(world, nbt.getCompoundTag("spell_data"));
			worldTime = nbt.getLong("world_time");
			expiry = nbt.getInteger("expiry");
		}
	}

	public static class DelayedObject implements INBTSerializable<NBTTagCompound> {

		private ModuleInstance module;
		private SpellRing spellRing;
		private SpellData spellData;
		private long worldTime;
		private int expiry;

		@NotNull
		private final World world;

		public DelayedObject(ModuleInstance module, SpellRing spellRing, SpellData spellData, long worldTime, int expiry) {
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

		public ModuleInstance getModule() {
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
				spellRing = SpellRingCache.INSTANCE.getSpellRingByNBT(nbt.getCompoundTag("spell_ring"));
			if (nbt.hasKey("spell_data"))
				spellData = SpellData.deserializeData(world, nbt.getCompoundTag("spell_data"));
			if (nbt.hasKey("world_time"))
				worldTime = nbt.getLong("world_time");
			if (nbt.hasKey("expiry"))
				expiry = nbt.getInteger("expiry");
			if (nbt.hasKey("module"))
				module = ModuleInstance.deserialize(nbt.getString("module"));
		}
	}
}
