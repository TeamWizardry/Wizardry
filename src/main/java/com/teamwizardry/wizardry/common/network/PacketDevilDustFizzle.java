package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

/**
 * Created by Demoniaque.
 */
public class PacketDevilDustFizzle extends PacketBase {

	@Save
	private Vec3d pos;
	@Save
	private int tick;

	public PacketDevilDustFizzle() {
	}

	public PacketDevilDustFizzle(Vec3d pos, int tick) {
		this.pos = pos;
		this.tick = tick;
	}

	@Override
	public void handle(MessageContext messageContext) {
		if (messageContext.side.isServer()) return;

		World world = LibrarianLib.PROXY.getClientPlayer().world;
		if (world == null) return;

		ClientRunnable.run(new ClientRunnable() {
			@Override
			@SideOnly(Side.CLIENT)
			public void runIfClient() {
				ParticleBuilder glitter = new ParticleBuilder(30);
				glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
				glitter.setMotionCalculationEnabled(true);
				glitter.setCollision(true);
				glitter.setCanBounce(true);

				ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 15, RandUtil.nextInt(10), (i, builder) -> {
					builder.setAcceleration(new Vec3d(0, RandUtil.nextDouble(-0.05, -0.01), 0));
					builder.setColor(new Color(RandUtil.nextFloat(0.9f, 1), RandUtil.nextFloat(0, 0.25f), 0));
					builder.setAlphaFunction(new InterpFadeInOut(0.0f, RandUtil.nextFloat(1f, 0.3f)));
					builder.setScaleFunction(new InterpScale(RandUtil.nextFloat(0.3f, 1f), RandUtil.nextFloat(0, 0.2f)));
					Vec3d offset = new Vec3d(RandUtil.nextDouble(-0.3, 0.3), RandUtil.nextDouble(-0.3, 0), RandUtil.nextDouble(-0.3, 0.3));
					builder.setPositionOffset(offset);
					builder.setLifetime(RandUtil.nextInt(20, 60));
					builder.setMotion(new Vec3d(RandUtil.nextDouble(-0.1, 0.1), RandUtil.nextDouble(0.1, 0.5), RandUtil.nextDouble(-0.1, 0.1)));
				});
			}
		});
	}
}
