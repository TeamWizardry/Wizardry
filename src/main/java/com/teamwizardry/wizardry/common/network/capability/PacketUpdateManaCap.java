package com.teamwizardry.wizardry.common.network.capability;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.api.capability.player.mana.IManaCapability;
import com.teamwizardry.wizardry.api.capability.player.mana.ManaCapabilityProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque on 8/16/2016.
 */
@PacketRegister(Side.CLIENT)
public class PacketUpdateManaCap extends PacketBase {

	@Save
	public NBTTagCompound tags;

	public PacketUpdateManaCap() {
	}

	public PacketUpdateManaCap(NBTTagCompound tag) {
		tags = tag;
	}

	@Override
	public void handle(@Nonnull MessageContext ctx) {
		if (ctx.side.isServer()) return;

		EntityPlayer player = LibrarianLib.PROXY.getClientPlayer();

		IManaCapability cap = ManaCapabilityProvider.getCap(player);

		if (cap != null) {
			cap.deserializeNBT(tags);
		}
	}
}
