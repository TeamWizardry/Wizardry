package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.api.LightningGenerator;
import com.teamwizardry.wizardry.client.render.LightningRenderer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;

/**
 * Created by LordSaad.
 */
public class PacketLightningBolt extends PacketBase {

	@Save
	private Vec3d point1;
	@Save
	private Vec3d point2;

	public PacketLightningBolt() {
	}

	public PacketLightningBolt(Vec3d point1, Vec3d point2) {
		this.point1 = point1;
		this.point2 = point2;
	}

	@Override
	public void handle(@NotNull MessageContext messageContext) {
		LightningRenderer.INSTANCE.points = new LightningGenerator(point1, point2).generate();
		LightningRenderer.INSTANCE.renderTick = 500;
	}
}
