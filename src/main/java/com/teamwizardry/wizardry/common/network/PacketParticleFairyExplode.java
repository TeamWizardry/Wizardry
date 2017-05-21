package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.awt.*;

/**
 * Created by LordSaad.
 */
public class PacketParticleFairyExplode extends PacketBase {

	@Save
	private Vec3d pos;
	@Save
	private Color color;
	@Save
	private Color color2;

	public PacketParticleFairyExplode() {
	}

	public PacketParticleFairyExplode(Vec3d pos, Color color, Color color2) {
		this.pos = pos;
		this.color = color;
		this.color2 = color2;
	}

	@Override
	public void handle(MessageContext messageContext) {
		if (messageContext.side.isServer()) return;
		if (Minecraft.getMinecraft().player == null) return;

		World world = Minecraft.getMinecraft().player.world;

		LibParticles.FAIRY_EXPLODE(world, pos, color, color2);
	}
}
