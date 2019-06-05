package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.common.module.effects.vanish.VanishTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@PacketRegister(Side.CLIENT)
public class PacketSyncVanish extends PacketBase {

	@Save
	public NBTTagCompound data;

	public PacketSyncVanish() {
	}

	public PacketSyncVanish(NBTTagCompound data) {
		this.data = data;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handle(@Nonnull MessageContext ctx) {
		VanishTracker.deserialize(data);
	}
}
