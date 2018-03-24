package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.api.capability.IWizardryCapability;
import com.teamwizardry.wizardry.api.capability.WizardryCapabilityProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque on 8/16/2016.
 */
@PacketRegister(Side.CLIENT)
public class PacketUpdateCaps extends PacketBase {


	@Save
	private NBTTagCompound tags;

	public PacketUpdateCaps() {
	}

	public PacketUpdateCaps(NBTTagCompound tag) {
		tags = tag;
	}

	@Override
	public void handle(@Nonnull MessageContext ctx) {
		EntityPlayer player = LibrarianLib.PROXY.getClientPlayer();

		IWizardryCapability cap = WizardryCapabilityProvider.getCap(player);

		if (cap != null) {
			cap.deserializeNBT(tags);
		}
	}
}
