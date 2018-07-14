package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.api.LightningGenerator;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RandUtilSeed;
import com.teamwizardry.wizardry.client.core.renderer.LightningRenderer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque.
 */
public class PacketRenderLightningBolt extends PacketBase {

	@Save
	private long seed;
	@Save
	private Vec3d from;
	@Save
	private Vec3d to;
	@Save
	private double offshootRange;
	
	public PacketRenderLightningBolt()
	{}

	public PacketRenderLightningBolt(long seed, Vec3d from, Vec3d to, double offshootRange) {
		this.from = from;
		this.to = to;
		this.seed = seed;
		this.offshootRange = offshootRange;
	}

	@Override
	public void handle(@Nonnull MessageContext messageContext) {
		LightningRenderer.addBolt(LightningGenerator.generate(new RandUtilSeed(seed), from, to, offshootRange), RandUtil.nextInt(10, 15));
	}
}
