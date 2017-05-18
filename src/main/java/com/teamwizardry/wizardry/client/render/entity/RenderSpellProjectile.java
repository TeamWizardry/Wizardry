package com.teamwizardry.wizardry.client.render.entity;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.util.InterpScale;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.Utils;
import com.teamwizardry.wizardry.common.entity.EntitySpellProjectile;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

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
		glitter.setCollision(true);
		glitter.setColorFunction(new InterpColorHSV(color, Color.WHITE));
		ParticleSpawner.spawn(glitter, entity.world, new StaticInterp<>(entity.getPositionVector()), 5, 0, (aFloat, particleBuilder) -> {
			glitter.setScaleFunction(new InterpScale((float) ThreadLocalRandom.current().nextDouble(0.3, 0.8), 0));
			glitter.setLifetime(ThreadLocalRandom.current().nextInt(10, 100));
		});
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntitySpellProjectile entity) {
		return null;
	}
}
