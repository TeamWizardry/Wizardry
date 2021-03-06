package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Created by Demoniaque.
 */
public class PacketExplode extends PacketBase {

	@Save
	public Vec3d pos;
	@Save
	public Color color1;
	@Save
	public Color color2;
	@Save
	public double strengthUpwards;
	@Save
	public double strengthSideways;
	@Save
	public int amount;
	@Save
	public int lifeTime;
	@Save
	public int lifeTimeRange;
	@Save
	public boolean bounce;

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
	public void handle(@NotNull MessageContext messageContext) {
		if (messageContext.side.isServer()) return;

		World world = LibrarianLib.PROXY.getClientPlayer().world;
		if (world == null) return;

		ClientRunnable.run(new ClientRunnable() {
			@Override
			@SideOnly(Side.CLIENT)
			public void runIfClient() {
				//	LibParticles.ParticleExplosion.INSTANCE.spawn(lifeTime, lifeTimeRange, amount, pos, color1, color2, strengthUpwards, strengthSideways);
				LibParticles.EXPLODE(world, pos, color1, color2, strengthSideways, strengthUpwards, amount, lifeTime, lifeTimeRange, bounce);
			}
		});
	}
}
