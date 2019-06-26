package com.teamwizardry.wizardry.common.network.capability;

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.api.capability.player.miscdata.IMiscCapability;
import com.teamwizardry.wizardry.api.capability.player.miscdata.MiscCapabilityProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque on 8/16/2016.
 */
@PacketRegister(Side.SERVER)
public class PacketUpdateMiscCapToServer extends PacketBase {

	@Save
	public NBTTagCompound tags;

	public PacketUpdateMiscCapToServer() {
	}

	public PacketUpdateMiscCapToServer(NBTTagCompound tag) {
		tags = tag;
	}

	@Override
	public void handle(@Nonnull MessageContext ctx) {

	}

	@Nullable
	@Override
	public PacketBase reply(@NotNull MessageContext ctx) {
		EntityPlayer player = ctx.getServerHandler().player;

		IMiscCapability cap = MiscCapabilityProvider.getCap(player);

		if (cap != null) {
			cap.deserializeNBT(tags);
		}

		return new PacketUpdateMiscCapToClient(tags);
	}
}
