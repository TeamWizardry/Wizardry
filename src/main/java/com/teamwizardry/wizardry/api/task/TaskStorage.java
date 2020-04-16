package com.teamwizardry.wizardry.api.task;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class TaskStorage implements INBTSerializable<CompoundNBT> {

	private static final String CHAINED_TO = "chained_to";

	/**
	 * The robot that this one is attached to.
	 * The robot that will control this one.
	 * Can be null.
	 */
	@Nullable
	public UUID chainedTo;

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
			nbt.putString(CHAINED_TO, chainedTo.toString());
		return null;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		if (nbt.contains(CHAINED_TO)) {
			chainedTo = UUID.fromString(nbt.getString(CHAINED_TO));
		}
	}
}
