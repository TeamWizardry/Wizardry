package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.common.network.PacketBase;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by LordSaad.
 */
public class PacketParticleAmbientFizz extends PacketBase {

	@Save
	private Vec3d pos;

	public PacketParticleAmbientFizz() {
	}

	public PacketParticleAmbientFizz(Vec3d pos) {
		this.pos = pos;
	}

	@Override
	public void handle(MessageContext messageContext) {
		if (messageContext.side.isServer()) return;
		if (Minecraft.getMinecraft().player == null) return;

		World world = Minecraft.getMinecraft().player.world;

		LibParticles.FIZZING_AMBIENT(world, pos);
	}
}
