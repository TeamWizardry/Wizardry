package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.common.entity.angel.zachriel.nemez.NemezTicker;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by LordSaad.
 */
@PacketRegister(Side.CLIENT)
public class PacketZachInterpolatePlayer extends PacketBase {

	@Save
	private int entityID;
	@Save
	private Vec3d to;
	@Save
	private float yaw;
	@Save
	private float pitch;

	public PacketZachInterpolatePlayer(int entityID, Vec3d to, float yaw, float pitch) {
		this.entityID = entityID;
		this.to = to;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public PacketZachInterpolatePlayer() {
	}

	@Override
	public void handle(MessageContext messageContext) {
		if (messageContext.side.isServer()) return;

		World world = LibrarianLib.PROXY.getClientPlayer().world;
		if (world == null) return;

		EntityLivingBase entity = (EntityLivingBase) world.getEntityByID(entityID);
		if (entity == null) return;

		entity.setPositionAndRotationDirect(to.x, to.y, to.z, yaw, pitch, 30, true);
		NemezTicker.INSTANCE.interpolatePosition(entity, entity.getPositionVector(), to, 100);
	}
}
