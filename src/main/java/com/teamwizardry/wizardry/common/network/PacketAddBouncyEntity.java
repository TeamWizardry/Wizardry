package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@PacketRegister(Side.SERVER)
public class PacketAddBouncyEntity extends PacketBase {

	@Save
	public UUID uuid;
	@Save
	public double bounce;

	public PacketAddBouncyEntity() {
	}

	public PacketAddBouncyEntity(EntityLivingBase living, double bounce) {
		this.uuid = living.getUniqueID();
		this.bounce = bounce;
	}

	@Override
	public void handle(@NotNull MessageContext ctx) {
		Entity entity = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityFromUuid(uuid);
		if (!(entity instanceof EntityLivingBase)) return;

		entity.fallDistance = 0;
		entity.isAirBorne = true;
		entity.onGround = false;
		entity.motionY = -entity.motionY;

		//	BounceHandler.addBounceHandler((EntityLivingBase) entity, bounce);
	}
}
