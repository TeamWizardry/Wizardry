package com.teamwizardry.wizardry.api.capability.player.mana;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

import static com.teamwizardry.wizardry.api.Constants.Data.*;

/**
 * Created by Demoniaque on 8/16/2016.
 */
public class ManaCapabilityStorage implements IStorage<IManaCapability> {

	public static final ManaCapabilityStorage INSTANCE = new ManaCapabilityStorage();

	@Override
	public NBTBase writeNBT(Capability<IManaCapability> capability, IManaCapability instance, EnumFacing side) {
		NBTTagCompound nbt = new NBTTagCompound();
		if (instance.getBloodType() != null)
			nbt.setString(BLOOD_TYPE, instance.getBloodType().id);
		nbt.setDouble(MAX_MANA, instance.getMaxMana());
		nbt.setDouble(MAX_BURNOUT, instance.getMaxBurnout());
		nbt.setDouble(MANA, instance.getMana());
		nbt.setDouble(BURNOUT, instance.getBurnout());

		return nbt;
	}

	@Override
	public void readNBT(Capability<IManaCapability> capability, IManaCapability instance, EnumFacing side, NBTBase nbt) {
		NBTTagCompound tag = (NBTTagCompound) nbt;

		instance.setMaxMana(tag.getDouble(MAX_MANA));
		instance.setMaxBurnout(tag.getDouble(MAX_BURNOUT));
		instance.setMana(tag.getDouble(MANA));
		instance.setBurnout(tag.getDouble(BURNOUT));
		if (tag.hasKey(BLOOD_TYPE))
			instance.setBloodType(EnumBloodType.getType(tag.getString(BLOOD_TYPE)));
	}
}
