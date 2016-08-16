package com.teamwizardry.wizardry.api.capability;

import com.teamwizardry.wizardry.api.capability.bloods.BloodRegistry;
import com.teamwizardry.wizardry.api.capability.bloods.IBloodType;
import com.teamwizardry.wizardry.common.network.MessageUpdateCapabilities;
import com.teamwizardry.wizardry.common.proxy.CommonProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Saad on 8/16/2016.
 */
public class DefaultWizardryCapability implements IWizardryCapability {

	int mana = 0, maxMana = 100, burnout = 0, maxBurnout = 100;
	IBloodType bloodType = BloodRegistry.HUMANBLOOD;

	@Override
	public int getMana() {
		return mana;
	}

	@Override
	public void setMana(int mana, EntityPlayer player) {
		this.mana = mana;
		if (mana < 0){
			this.mana = 0;
		}
		if (mana > getMaxMana()){
			this.mana = getMaxMana();
		}
		dataChanged(player);
	}

	@Override
	public int getMaxMana() {
		return maxMana;
	}

	@Override
	public void setMaxMana(int maxMana, EntityPlayer player) {
		this.maxMana = maxMana;
		dataChanged(player);
	}

	@Override
	public int getBurnout() {
		return burnout;
	}

	@Override
	public void setBurnout(int burnout, EntityPlayer player) {
		this.burnout = burnout;
		if (burnout < 0){
			this.burnout = 0;
		}
		if (burnout > getMaxBurnout()){
			this.burnout = getMaxBurnout();
		}
		dataChanged(player);
	}

	@Override
	public int getMaxBurnout() {
		return maxBurnout;
	}

	@Override
	public void setMaxBurnout(int maxBurnout, EntityPlayer player) {
		this.maxBurnout = maxBurnout;
		dataChanged(player);
	}

	@Override
	public IBloodType getBloodType() {
		return bloodType;
	}

	@Override
	public void setBloodType(IBloodType bloodType, EntityPlayer player) {
		this.bloodType = bloodType;
		dataChanged(player);
	}

	@Override
	public NBTTagCompound saveNBTData() {
		return (NBTTagCompound)WizardryCapabilityStorage.INSTANCE.writeNBT(WizardryCapabilityProvider.wizardryCapability, this, null);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		WizardryCapabilityStorage.INSTANCE.readNBT(WizardryCapabilityProvider.wizardryCapability, this, null, compound);

	}

	@Override
	public void dataChanged(EntityPlayer player) {
		if(player != null && !player.getEntityWorld().isRemote){
			CommonProxy.NETWORK.sendTo(new MessageUpdateCapabilities(saveNBTData()), (EntityPlayerMP) player);
		}
	}
}
