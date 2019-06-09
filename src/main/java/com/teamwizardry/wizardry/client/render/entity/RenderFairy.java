package com.teamwizardry.wizardry.client.render.entity;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque on 8/25/2016.
 */
public class RenderFairy extends RenderLiving<EntityFairy> {

	public RenderFairy(RenderManager renderManager, ModelBase modelBase) {
		super(renderManager, modelBase, 0.0f);
	}

	@Override
	public boolean canRenderName(EntityFairy entity) {
		return false;
	}

	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntityFairy entity) {
		return null;
	}

	@Override
	public void doRender(@NotNull EntityFairy entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);

		LibParticles.FAIRY_HEAD(entity.world, entity.getPositionVector().add(0, 0.25, 0), entity.getColor());

		ParticleBuilder glitter = new ParticleBuilder(entity.getAge());
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFloatInOut(0.2f, 1f));

		if (RandUtil.nextInt(2) == 0)
			ParticleSpawner.spawn(glitter, entity.world, new StaticInterp<>(entity.getPositionVector()), 1, 0, (i, build) -> {
				build.setMotion(new Vec3d(RandUtil.nextDouble(-0.01, 0.01), RandUtil.nextDouble(-0.01, 0.01), RandUtil.nextDouble(-0.01, 0.01)));
				if (RandUtil.nextBoolean())
					build.setColor(entity.getColor());
				else build.setColor(entity.getColor());
				if (entity.isDulled()) {
					build.setCollision(true);
					build.enableMotionCalculation();
				}
			});
	}
}
