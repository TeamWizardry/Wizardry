package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.awt.Color;

/**
 * Created by LordSaad.
 */
public class PacketExplode extends PacketBase {

	@Save
	private Vec3d pos;
	@Save
	private Color color1;
	@Save
	private Color color2;
	@Save
	private double strengthUpwards;
	@Save
	private double strengthSideways;
	@Save
	private int amount;
	@Save
	private int lifeTime;
	@Save
	private int lifeTimeRange;
	@Save
	private boolean bounce;

	public PacketExplode() {
	}

	public PacketExplode(Vec3d pos, Color color1, Color color2, double strengthUpwards, double strengthSideways, int amount, int lifeTime, int lifeTimeRange, boolean bounce) {
		this.pos = pos;
		this.color1 = color1;
		this.color2 = color2;
		this.strengthUpwards = strengthUpwards;
		this.strengthSideways = strengthSideways;
		this.amount = amount;
		this.lifeTime = lifeTime;
		this.lifeTimeRange = lifeTimeRange;
		this.bounce = bounce;
	}

	@Override
	public void handle(MessageContext messageContext) {
		if (messageContext.side.isServer()) return;
		if (Minecraft.getMinecraft().player == null) return;

		World world = Minecraft.getMinecraft().player.world;

		LibParticles.EXPLODE(world, pos, color1, color2, strengthSideways, strengthUpwards, amount, lifeTime, lifeTimeRange, bounce);
	}
}
