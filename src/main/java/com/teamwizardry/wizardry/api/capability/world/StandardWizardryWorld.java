package com.teamwizardry.wizardry.api.capability.world;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.SpellObjectManager;
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
import java.util.Map;
import java.util.UUID;

public class StandardWizardryWorld implements WizardryWorld {

	private World world;
	private SpellObjectManager spellObjectManager = new SpellObjectManager();
	public HashMap<BlockPos, NemezTracker> blockNemezDrives = new HashMap<>();
	public HashMap<UUID, NemezTracker> entityNemezDrives = new HashMap<>();

	public static StandardWizardryWorld create(World world) {
		StandardWizardryWorld wizardryWorld = new StandardWizardryWorld();
		wizardryWorld.world = world;
		return wizardryWorld;
	}

	public static StandardWizardryWorld create() {
		return new StandardWizardryWorld();
	}

	//		PacketHandler.NETWORK.sendToDimension(new PacketSyncWizardryWorld(serializeNBT()), world.provider.getDimension());
	@Override
	public SpellObjectManager getSpellObjectManager() {
		return spellObjectManager;
	}

	@Override
	public NemezTracker addNemezDrive(BlockPos pos, NemezTracker nemezDrive) {
		blockNemezDrives.put(pos, nemezDrive);

		PacketHandler.NETWORK.sendToDimension(new PacketSyncWizardryWorld(serializeNBT()), world.provider.getDimension());

		return nemezDrive;
	}

	@Override
	public NemezTracker addNemezDrive(UUID uuid, NemezTracker nemezDrive) {
		entityNemezDrives.put(uuid, nemezDrive);

		PacketHandler.NETWORK.sendToDimension(new PacketSyncWizardryWorld(serializeNBT()), world.provider.getDimension());

		return nemezDrive;
	}

	@Override
	public void removeNemezDrive(BlockPos pos) {
		blockNemezDrives.remove(pos);

		PacketHandler.NETWORK.sendToDimension(new PacketSyncWizardryWorld(serializeNBT()), world.provider.getDimension());
	}

	@Override
	public void removeNemezDrive(UUID uuid) {
		entityNemezDrives.remove(uuid);

		PacketHandler.NETWORK.sendToDimension(new PacketSyncWizardryWorld(serializeNBT()), world.provider.getDimension());
	}

	@Override
	public HashMap<BlockPos, NemezTracker> getBlockNemezDrives() {
		return blockNemezDrives;
	}

	@Override
	public HashMap<UUID, NemezTracker> getEntityNemezDrives() {
		return entityNemezDrives;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();

		compound.setTag("spell_object_manager", spellObjectManager.manager.toNbt());

		NBTTagList driveNBT = new NBTTagList();
		for (Map.Entry<BlockPos, NemezTracker> entry : blockNemezDrives.entrySet()) {
			if (entry == null) continue;
			NBTTagCompound compound1 = new NBTTagCompound();
			compound1.setLong("pos", entry.getKey().toLong());
			compound1.setTag("drive", entry.getValue().serializeNBT());
			driveNBT.appendTag(compound1);
		}

		compound.setTag("drives", driveNBT);
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound compound) {
		if (compound.hasKey("spell_object_manager")) {
			spellObjectManager = new SpellObjectManager();
			spellObjectManager.manager.fromNbt(compound.getCompoundTag("spell_object_manager"));
		}

		if (compound.hasKey("drives")) {
			NBTTagList driveNBT = compound.getTagList("drives", Constants.NBT.TAG_COMPOUND);
			for (NBTBase base : driveNBT) {
				if (base instanceof NBTTagCompound) {
					if (((NBTTagCompound) base).hasKey("pos") && ((NBTTagCompound) base).hasKey("drive")) {
						BlockPos pos = BlockPos.fromLong(((NBTTagCompound) base).getLong("pos"));
						NemezTracker tracker = new NemezTracker();
						tracker.deserializeNBT(((NBTTagCompound) base).getTagList("drive", Constants.NBT.TAG_COMPOUND));

						blockNemezDrives.put(pos, tracker);
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
