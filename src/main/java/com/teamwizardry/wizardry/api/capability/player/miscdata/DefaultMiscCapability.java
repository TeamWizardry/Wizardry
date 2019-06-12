package com.teamwizardry.wizardry.api.capability.player.miscdata;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import com.teamwizardry.wizardry.common.network.PacketUpdateMiscCap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public class DefaultMiscCapability implements IMiscCapability {

	@Nullable
	private UUID selectedFairy = null;

	@Override
	public void setSelectedFairy(@Nullable UUID selectedFairy) {
		this.selectedFairy = selectedFairy;
	}

	@Override
	@Nullable
	public EntityFairy getSelectedFairyEntity(World world) {
		if (selectedFairy == null) return null;

		for (EntityFairy entityFairy : world.getEntities(EntityFairy.class, input -> {
			if (input != null) {
				return input.getUniqueID().equals(selectedFairy);
			}
			return false;
		})) {
			if (entityFairy.getUniqueID().equals(selectedFairy)) return entityFairy;
		}

		return null;
	}

	@Nullable
	@Override
	public UUID getSelectedFairyUUID() {
		return selectedFairy;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return (NBTTagCompound) MiscCapabilityStorage.INSTANCE.writeNBT(MiscCapabilityProvider.miscCapability, this, null);
	}

	@Override
	public void deserializeNBT(NBTTagCompound compound) {
		MiscCapabilityStorage.INSTANCE.readNBT(MiscCapabilityProvider.miscCapability, this, null, compound);
	}

	@Override
	public void dataChanged(Entity entity) {
		if (entity instanceof EntityPlayer && !entity.getEntityWorld().isRemote)
			PacketHandler.NETWORK.sendTo(new PacketUpdateMiscCap(serializeNBT()), (EntityPlayerMP) entity);
	}
}
