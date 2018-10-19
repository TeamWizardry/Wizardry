package com.teamwizardry.wizardry.api.capability.world;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.spell.IDelayedModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstance;
import com.teamwizardry.wizardry.common.core.SpellTicker;
import com.teamwizardry.wizardry.common.core.nemez.NemezTracker;
import com.teamwizardry.wizardry.common.network.PacketSyncWizardryWorld;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class StandardWizardryWorld implements WizardryWorld {

	private World world;
	private HashSet<SpellTicker.LingeringObject> lingeringStorageSet = new HashSet<>();
	private HashSet<SpellTicker.DelayedObject> delayedStorageSet = new HashSet<>();
	public HashMap<BlockPos, NemezTracker> nemezDrives = new HashMap<>();

	public static StandardWizardryWorld create(World world) {
		StandardWizardryWorld wizardryWorld = new StandardWizardryWorld();
		wizardryWorld.world = world;
		return wizardryWorld;
	}

	public static StandardWizardryWorld create() {
		return new StandardWizardryWorld();
	}

	@Override
	public void addLingerSpell(SpellRing spellRing, SpellData data, int expiry) {
		lingeringStorageSet.add(new SpellTicker.LingeringObject(spellRing, data, data.world.getTotalWorldTime(), expiry));

		PacketHandler.NETWORK.sendToDimension(new PacketSyncWizardryWorld(serializeNBT()), data.world.provider.getDimension());
	}

	@Override
	public void addDelayedSpell(ModuleInstance module, SpellRing spellRing, SpellData data, int expiry) {
		if (module.getModuleClass() instanceof IDelayedModule)
			delayedStorageSet.add(new SpellTicker.DelayedObject(module, spellRing, data, data.world.getTotalWorldTime(), expiry));

		PacketHandler.NETWORK.sendToDimension(new PacketSyncWizardryWorld(serializeNBT()), data.world.provider.getDimension());
	}

	@Override
	public NemezTracker addNemezDrive(BlockPos pos, NemezTracker nemezDrive) {
		nemezDrives.put(pos, nemezDrive);

		PacketHandler.NETWORK.sendToDimension(new PacketSyncWizardryWorld(serializeNBT()), world.provider.getDimension());

		return nemezDrive;
	}

	@Override
	public void removeNemezDrive(BlockPos pos) {
		nemezDrives.remove(pos);

		PacketHandler.NETWORK.sendToDimension(new PacketSyncWizardryWorld(serializeNBT()), world.provider.getDimension());
	}

	@Override
	public HashMap<BlockPos, NemezTracker> getNemezDrives() {
		return nemezDrives;
	}

	@Override
	public HashSet<SpellTicker.LingeringObject> getLingeringObjects() {
		return lingeringStorageSet;
	}

	@Override
	public HashSet<SpellTicker.DelayedObject> getDelayedObjects() {
		return delayedStorageSet;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();

		NBTTagList lingeringNBT = new NBTTagList();
		for (SpellTicker.LingeringObject object : lingeringStorageSet) {
			if (object == null) continue;
			lingeringNBT.appendTag(object.serializeNBT());
		}

		NBTTagList delayedNBT = new NBTTagList();
		for (SpellTicker.DelayedObject object : delayedStorageSet) {
			if (object == null) continue;
			delayedNBT.appendTag(object.serializeNBT());
		}

		NBTTagList driveNBT = new NBTTagList();
		for (Map.Entry<BlockPos, NemezTracker> entry : nemezDrives.entrySet()) {
			if (entry == null) continue;
			NBTTagCompound compound1 = new NBTTagCompound();
			compound1.setLong("pos", entry.getKey().toLong());
			compound1.setTag("drive", entry.getValue().serializeNBT());
			driveNBT.appendTag(compound1);
		}

		compound.setTag("lingering", lingeringNBT);
		compound.setTag("delayed", delayedNBT);
		compound.setTag("drives", driveNBT);
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound compound) {
		if (compound.hasKey("lingering")) {
			NBTTagList lingeringNBT = compound.getTagList("lingering", Constants.NBT.TAG_COMPOUND);
			for (NBTBase base : lingeringNBT) {
				if (base instanceof NBTTagCompound) {
					lingeringStorageSet.add(SpellTicker.LingeringObject.deserialize(world, (NBTTagCompound) base));
				}
			}
		}

		if (compound.hasKey("delayed")) {
			NBTTagList delayedNBT = compound.getTagList("delayed", Constants.NBT.TAG_COMPOUND);
			for (NBTBase base : delayedNBT) {
				if (base instanceof NBTTagCompound) {
					delayedStorageSet.add(SpellTicker.DelayedObject.deserialize(world, (NBTTagCompound) base));
				}
			}
		}

		if (compound.hasKey("drives")) {
			NBTTagList driveNBT = compound.getTagList("drives", Constants.NBT.TAG_COMPOUND);
			for (NBTBase base : driveNBT) {
				if (base instanceof NBTTagCompound) {
					if (((NBTTagCompound) base).hasKey("pos") && ((NBTTagCompound) base).hasKey("drive")) {
						BlockPos pos = BlockPos.fromLong(((NBTTagCompound) base).getLong("pos"));
						NemezTracker tracker = new NemezTracker();
						tracker.deserializeNBT(((NBTTagCompound) base).getTagList("drive", Constants.NBT.TAG_COMPOUND));

						nemezDrives.put(pos, tracker);
					}
				}
			}
		}
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == WizardryWorldCapability.capability();
	}

	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		return capability == WizardryWorldCapability.capability() ? WizardryWorldCapability.capability().cast(this) : null;
	}
}
