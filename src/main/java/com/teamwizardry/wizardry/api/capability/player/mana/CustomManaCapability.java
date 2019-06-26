package com.teamwizardry.wizardry.api.capability.player.mana;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.saving.Savable;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.common.network.capability.PacketUpdateManaCap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

/**
 * Created by Demoniaque.
 */
@Savable
public class CustomManaCapability implements IManaCapability {

	@Save
	double mana = 0;
	@Save
	double maxMana = 100;
	@Save
	double burnout = 100;
	@Save
	double maxBurnout = 100;
	@Save
	EnumBloodType bloodType;

	CustomManaCapability() {
	}

	public CustomManaCapability(double maxMana, double maxBurnout) {
		this.maxMana = maxMana;
		this.maxBurnout = maxBurnout;
	}

	public CustomManaCapability(double maxMana, double maxBurnout, double mana, double burnout) {
		this.maxMana = maxMana;
		this.maxBurnout = maxBurnout;
		this.mana = mana;
		this.burnout = burnout;
	}

	@Override
	public double getMana() {
		return mana;
	}

	@Override
	public void setMana(double mana) {
		this.mana = mana;
		if (mana < 0) {
			this.mana = 0;
		}
		if (mana > maxMana) {
			this.mana = maxMana;
		}
	}

	@Override
	public double getMaxMana() {
		return maxMana;
	}

	@Override
	public void setMaxMana(double maxMana) {
		this.maxMana = maxMana;
	}

	@Override
	public double getBurnout() {
		return burnout;
	}

	@Override
	public void setBurnout(double burnout) {
		this.burnout = burnout;
		if (burnout < 0) {
			this.burnout = 0;
		}
		if (burnout > maxBurnout) {
			this.burnout = maxBurnout;
		}
	}

	@Override
	public double getMaxBurnout() {
		return maxBurnout;
	}

	@Override
	public void setMaxBurnout(double maxBurnout) {
		this.maxBurnout = maxBurnout;
	}

	@Override
	@Nullable
	public EnumBloodType getBloodType() {
		return bloodType;
	}

	@Override
	public void setBloodType(EnumBloodType bloodType) {
		this.bloodType = bloodType;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return (NBTTagCompound) ManaCapabilityStorage.INSTANCE.writeNBT(ManaCapabilityProvider.manaCapability, this, null);
	}

	@Override
	public void deserializeNBT(NBTTagCompound compound) {
		ManaCapabilityStorage.INSTANCE.readNBT(ManaCapabilityProvider.manaCapability, this, null, compound);
	}

	@Override
	public void dataChanged(Entity entity) {
		if ((entity != null) && entity instanceof EntityPlayer && !entity.getEntityWorld().isRemote)
			PacketHandler.NETWORK.sendTo(new PacketUpdateManaCap(serializeNBT()), (EntityPlayerMP) entity);
	}
}
