package com.teamwizardry.wizardry.client.render.block;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.features.tesr.TileRenderHandler;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.item.HaloInfusionItemRegistry;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.tile.TileHaloInfuser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class TileHaloInfuserRenderer extends TileRenderHandler<TileHaloInfuser> {

	public TileHaloInfuserRenderer(@NotNull TileHaloInfuser tile) {
		super(tile);
	}

	@Override
	public void render(float partialTicks, int destroyStage, float alpha) {

		ParticleBuilder glitter = new ParticleBuilder(20);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(1f, 1f));
		glitter.setColor(new Color(0x0022FF));
		glitter.disableRandom();

		double centerX = tile.getPos().getX() + 0.5;
		double centerZ = tile.getPos().getZ() + 0.5;
		double radius = 3;
		int count = HaloInfusionItemRegistry.INSTANCE.items.size();
		for (int i = 0; i < count; i++) {

			float angle = (float) (i * Math.PI * 2.0 / count);
			double x = (centerX + MathHelper.cos(angle) * radius);
			double z = (centerZ + MathHelper.sin(angle) * radius);

			if (RandUtil.nextInt(20) == 0) {
				ParticleSpawner.spawn(glitter, tile.getWorld(), new StaticInterp<>(new Vec3d(x, tile.getPos().getY() + 3, z)), 1, 0, (aFloat, build) -> {
					build.setScale(RandUtil.nextFloat(4, 5));
					build.setPositionOffset(new Vec3d(
							RandUtil.nextDouble(-0.1, 0.1),
							RandUtil.nextDouble(-0.1, 0.1),
							RandUtil.nextDouble(-0.1, 0.1)
					));
					build.setLifetime(RandUtil.nextInt(40, 50));
				});
			}

			HaloInfusionItemRegistry.HaloInfusionItem item = HaloInfusionItemRegistry.INSTANCE.items.get(i);

			GlStateManager.pushMatrix();
			GlStateManager.translate(-tile.getPos().getX(), -tile.getPos().getY(), -tile.getPos().getZ());
			GlStateManager.translate(x, tile.getPos().getY() + 3, z);

			double depthRadius = 0.5;
			Vec3d playerSub = Minecraft.getMinecraft().player.getPositionVector().subtract(new Vec3d(x, tile.getPos().getY() + 3, z));
			Vec3d restricted = playerSub.scale(depthRadius / playerSub.lengthVector());
			restricted = new Vec3d(restricted.x, restricted.y / 2.0, restricted.z);

			GlStateManager.translate(restricted.x, restricted.y, restricted.z);
			GlStateManager.scale(0.4, 0.4, 0.4);
			GlStateManager.rotate(Minecraft.getMinecraft().player.rotationYaw * -1, 0, 1, 0);
			GlStateManager.rotate(Minecraft.getMinecraft().player.rotationPitch, 1, 0, 0);
			Minecraft.getMinecraft().getRenderItem().renderItem(item.getStack(), ItemCameraTransforms.TransformType.NONE);
			GlStateManager.popMatrix();
		}
	}
}
