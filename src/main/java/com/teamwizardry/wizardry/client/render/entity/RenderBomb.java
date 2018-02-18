package com.teamwizardry.wizardry.client.render.entity;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import com.teamwizardry.wizardry.common.entity.EntityBomb;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

import static com.teamwizardry.wizardry.common.entity.EntityBomb.DATA_BOMB_TYPE;

public class RenderBomb extends Render<EntityBomb> {

	public RenderBomb(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(@Nonnull EntityBomb entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
		int type = entity.getDataManager().get(DATA_BOMB_TYPE);

		Color color, color2;
		if (type == 1) {
			color = Color.CYAN;
			color2 = Color.BLUE;
		} else {
			color = Color.RED;
			color2 = Color.ORANGE;
		}

		ParticleBuilder glitter = new ParticleBuilder(10);
		glitter.setAlphaFunction(new InterpFadeInOut(0f, 0.3f));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.enableMotionCalculation();
		glitter.setCollision(true);
		glitter.setCanBounce(true);
		glitter.setAcceleration(new Vec3d(0, -0.015, 0));
		ParticleSpawner.spawn(glitter, entity.world, new StaticInterp<>(entity.getPositionVector().add(new Vec3d(entity.motionX, entity.motionY, entity.motionZ))), 5, 0, (aFloat, particleBuilder) -> {
			particleBuilder.setScaleFunction(new InterpScale((float) RandUtil.nextDouble(0.3, 0.8), 0));
			particleBuilder.setLifetime(RandUtil.nextInt(40, 60));
			particleBuilder.addMotion(new Vec3d(
					RandUtil.nextDouble(-0.03, 0.03),
					RandUtil.nextDouble(-0.01, 0.05),
					RandUtil.nextDouble(-0.03, 0.03)
			));
			if (RandUtil.nextBoolean()) {
				particleBuilder.setColor(color);
			} else {
				particleBuilder.setColor(color2);
			}
		});

		glitter.disableMotionCalculation();
		glitter.setMotion(Vec3d.ZERO);
		glitter.setLifetime(20);
		glitter.setScaleFunction(new InterpFadeInOut(0f, 1f));
		glitter.setAlphaFunction(new InterpFadeInOut(0f, 1f));
		ParticleSpawner.spawn(glitter, entity.world, new StaticInterp<>(entity.getPositionVector()), 5, 0, (aFloat, particleBuilder) -> {
			particleBuilder.setScale(RandUtil.nextFloat(0.5f, 2));
			particleBuilder.setLifetime(RandUtil.nextInt(5, 10));
			//particleBuilder.addMotion()
		});
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntityBomb entity) {
		return null;
	}
}
