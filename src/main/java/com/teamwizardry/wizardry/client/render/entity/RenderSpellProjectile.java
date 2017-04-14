package com.teamwizardry.wizardry.client.render.entity;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.common.util.math.interpolate.position.InterpHelix;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import com.teamwizardry.wizardry.common.entity.EntitySpellProjectile;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Saad on 8/25/2016.
 */
public class RenderSpellProjectile extends RenderLiving<EntitySpellProjectile> {

	public RenderSpellProjectile(RenderManager renderManager, ModelBase modelBase) {
		super(renderManager, modelBase, 0.0f);
	}

	@Override
	public void doRender(@Nonnull EntitySpellProjectile entity, double x, double y, double z, float entityYaw, float partialTicks) {
		if (entity.module == null) return;
		if (entity.module.getColor() == null) return;
		ParticleBuilder glitter = new ParticleBuilder(10);
		glitter.setColor(new Color(1.0f, 1.0f, 1.0f, 0.1f));
		glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.setColor(entity.module.getColor());

		ParticleSpawner.spawn(glitter, entity.world, new StaticInterp<>(entity.getPositionVector()), 10, 0, (aFloat, particleBuilder) -> {
			glitter.setScale((float) ThreadLocalRandom.current().nextDouble(0.3, 0.8));
			glitter.setLifetime(ThreadLocalRandom.current().nextInt(10, 20));
			glitter.setPositionFunction(new InterpHelix(Vec3d.ZERO, entity.getLook(0), 0.3f, 0.3f, 1F, ThreadLocalRandom.current().nextFloat()));
		});
		LibParticles.FAIRY_HEAD(entity.world, entity.getPositionVector(), entity.module.getColor());
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntitySpellProjectile entity) {
		return null;
	}
}
