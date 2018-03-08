package com.teamwizardry.wizardry.client.render.block;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.features.tesr.TileRenderHandler;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import com.teamwizardry.wizardry.common.tile.TileHaloInfuser;
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

		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0f, 1f));
		glitter.setAcceleration(new Vec3d(0, RandUtil.nextDouble(-0.001, 0.001), 0));
		glitter.setColor(new Color(0x0022FF));

		double centerX = tile.getPos().getX() + 0.5;
		double centerZ = tile.getPos().getZ() + 0.5;
		double radius = 3;
		int count = 5;
		for (int i = 0; i < count; i++) {
			if (RandUtil.nextInt(10) != 0) continue;

			float angle = (float) (i * Math.PI * 2.0 / count);
			double x = (centerX + MathHelper.cos(angle) * radius);
			double z = (centerZ + MathHelper.sin(angle) * radius);

			ParticleSpawner.spawn(glitter, tile.getWorld(), new StaticInterp<>(new Vec3d(x, tile.getPos().getY() + 2, z)), 1, 0, (aFloat, build) -> {
				glitter.setLifetime(RandUtil.nextInt(40, 70));
				glitter.setScaleFunction(new InterpScale(RandUtil.nextFloat(3.5f, 4), RandUtil.nextFloat(0, 1)));
				glitter.setPositionOffset(new Vec3d(
						RandUtil.nextDouble(-0.1, 0.1),
						RandUtil.nextDouble(-0.1, 0.1),
						RandUtil.nextDouble(-0.1, 0.1)));
			});
		}
	}
}
