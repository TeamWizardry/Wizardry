package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.common.entity.angel.zachriel.Tardis;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by LordSaad.
 */
@PacketRegister(Side.CLIENT)
public class PacketZachClearCompanions extends PacketBase {

	@Save
	private int entityID;

	public PacketZachClearCompanions() {
	}

	public PacketZachClearCompanions(int entityID) {
		this.entityID = entityID;
	}

	@Override
	public void handle(MessageContext messageContext) {
		if (messageContext.side.isServer()) return;

		World world = LibrarianLib.PROXY.getClientPlayer().world;
		if (world == null) return;

		EntityLivingBase entity = (EntityLivingBase) world.getEntityByID(entityID);
		if (entity == null) return;

		Tardis.INSTANCE.clearCompanions(entity);
	}
}
