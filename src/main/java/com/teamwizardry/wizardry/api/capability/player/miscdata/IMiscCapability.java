package com.teamwizardry.wizardry.api.capability.player.miscdata;

import com.teamwizardry.wizardry.common.entity.EntityFairy;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.UUID;

public interface IMiscCapability extends INBTSerializable<NBTTagCompound> {

	void setSelectedFairy(@Nullable UUID fairyUUID);

	@Nullable
	EntityFairy getSelectedFairyEntity(World world);

	@Nullable
	UUID getSelectedFairyUUID();

	void dataChanged(Entity player);

}
