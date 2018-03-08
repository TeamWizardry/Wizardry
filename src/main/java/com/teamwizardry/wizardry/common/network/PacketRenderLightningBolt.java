package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.api.LightningGenerator;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RandUtilSeed;
import com.teamwizardry.wizardry.client.core.LightningRenderer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque.
 */
public class PacketRenderLightningBolt extends PacketBase {

	@Save
	private Vec3d point1;
	@Save
	private Vec3d point2;
	@Save
	private long seed;

	public PacketRenderLightningBolt() {
	}

	public PacketRenderLightningBolt(Vec3d point1, Vec3d point2, long seed) {
		this.point1 = point1;
		this.point2 = point2;
		this.seed = seed;
	}

	@Override
	public void handle(@Nonnull MessageContext messageContext) {
		LightningRenderer.INSTANCE.addBolt(new LightningGenerator(point1, point2, new RandUtilSeed(seed)).generate(), RandUtil.nextInt(30, 40));
	}
}
