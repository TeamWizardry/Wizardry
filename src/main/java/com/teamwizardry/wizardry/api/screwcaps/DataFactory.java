package com.teamwizardry.wizardry.api.screwcaps;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Saad on 8/17/2016.
 */
public class DataFactory {

	public static final DataFactory INSTANCE = new DataFactory();

	private final String PLAYER_TAG = "PlayerPersisted";

	private DataFactory() {
	}

	public WizardryData getPlayerData(EntityPlayer player) {
		if (player.getEntityData().hasKey(PLAYER_TAG))
			return new WizardryData(player.getEntityData().getCompoundTag(PLAYER_TAG));
		return null;
	}

	public void setPlayerData(EntityPlayer player, NBTTagCompound compound) {
		player.getEntityData().setTag(PLAYER_TAG, compound);
	}

	public void setPlayerData(EntityPlayer player, WizardryData data) {
		player.getEntityData().setTag(PLAYER_TAG, data.toNBT());
	}

	public boolean doesPlayerHaveData(EntityPlayer player) {
		return player.getEntityData().hasKey(PLAYER_TAG);
	}

	public WizardryData setDefaultPlayerData(EntityPlayer player) {
		WizardryData data = new WizardryData();
		player.getEntityData().setTag(PLAYER_TAG, data.toNBT());
		//Minecraft.getMinecraft().thePlayer.sendChatMessage(data + "");
		return data;
	}

	public WizardryData getDefaultData() {
		return new WizardryData();
	}

	public WizardryData convertNBTToData(NBTTagCompound compound) {
		return new WizardryData(compound);
	}

	public NBTTagCompound convertDataToNBT(WizardryData data) {
		return data.toNBT();
	}
}
