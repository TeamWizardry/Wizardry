package com.teamwizardry.wizardry.client.render.entity;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.entity.angel.zachriel.EntityZachriel;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque on 8/21/2016.
 */
public class RenderZachriel extends RenderLiving<EntityZachriel> {

	public static final ResourceLocation SPIRIT_TEX = new ResourceLocation(Wizardry.MODID, "textures/entity/gavreel.png");

	public RenderZachriel(RenderManager renderManager, ModelBase modelBase) {
		super(renderManager, modelBase, 0.0f);
	}

	@Override
	public boolean canRenderName(EntityZachriel entity) {
		return true;
	}

	@Nonnull
	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntityZachriel entity) {
		return new ResourceLocation(Wizardry.MODID, "textures/capes/cape_9417e56860544a47b1b593f747cfa4ce.png");
	}
}
