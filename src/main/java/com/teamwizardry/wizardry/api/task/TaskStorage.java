package com.teamwizardry.wizardry.api.task;

import com.teamwizardry.wizardry.api.StringConsts;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * A class holding data in TaskController. Data is shared and accessible to all tasks in the TaskController.
 */
public class TaskStorage implements INBTSerializable<CompoundNBT> {

	/**
	 * The robot that this one is attached to.
	 * The robot that will control this one.
	 * Can be null.
	 */
	@Nullable
	public UUID chainedTo;

	/**
	 * Save and retrieve all the data you want. It will be properly saved and stored.
	 */
	public CompoundNBT storageNBT = new CompoundNBT();

	@Nonnull
	public static TaskStorage deserializeFromEntity(Entity entity, DataParameter<CompoundNBT> dataParameter) {
		TaskStorage storage = new TaskStorage();

		CompoundNBT nbt = entity.getDataManager().get(dataParameter);
		storage.deserializeNBT(nbt);
		return storage;
	}

	@Nonnull
	public static TaskStorage deserialize(CompoundNBT nbt) {
		TaskStorage storage = new TaskStorage();
		storage.deserializeNBT(nbt);
		return storage;
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();

		if (chainedTo != null)
			nbt.putString(StringConsts.CHAINED_TO, chainedTo.toString());

		if (storageNBT != null)
			nbt.put(StringConsts.STORAGE_NBT, storageNBT);
		return null;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		if (nbt.contains(StringConsts.CHAINED_TO)) {
			chainedTo = UUID.fromString(nbt.getString(StringConsts.CHAINED_TO));
		}

		if (nbt.contains(StringConsts.STORAGE_NBT)) {
			storageNBT = nbt.getCompound(StringConsts.STORAGE_NBT);
		}
	}
}
