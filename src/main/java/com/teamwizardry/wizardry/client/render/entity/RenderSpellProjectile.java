package com.teamwizardry.wizardry.client.render.entity;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import com.teamwizardry.wizardry.common.entity.EntitySpellProjectile;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.teamwizardry.wizardry.common.entity.EntitySpellProjectile.DATA_COLOR;

/**
 * Created by Saad on 8/25/2016.
 */
public class RenderSpellProjectile extends Render<EntitySpellProjectile> {

	public RenderSpellProjectile(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(@Nonnull EntitySpellProjectile entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
		Color color = new Color(entity.getDataManager().get(DATA_COLOR), true);
		ParticleBuilder glitter = new ParticleBuilder(10);
		glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.setColor(color);
		glitter.enableMotionCalculation();
		glitter.setCollision(true);
		ParticleSpawner.spawn(glitter, entity.world, new StaticInterp<>(entity.getPositionVector()), 1, 0, (aFloat, particleBuilder) -> {
			glitter.setScale((float) ThreadLocalRandom.current().nextDouble(0.3, 0.8));
			glitter.setLifetime(ThreadLocalRandom.current().nextInt(10, 20));
		});
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntitySpellProjectile entity) {
		return null;
	}
}
