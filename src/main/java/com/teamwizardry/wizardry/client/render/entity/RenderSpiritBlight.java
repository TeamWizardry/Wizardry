package com.teamwizardry.wizardry.client.render.entity;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.entity.EntitySpiritBlight;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque on 8/21/2016.
 */
public class RenderSpiritBlight extends RenderLiving<EntitySpiritBlight> {

	public static final ResourceLocation SPIRIT_TEX = new ResourceLocation(Wizardry.MODID, "textures/entity/spirit_wight.png");

	public RenderSpiritBlight(RenderManager renderManager, ModelBase modelBase) {
		super(renderManager, modelBase, 0.0f);
	}

	@Override
	public boolean canRenderName(EntitySpiritBlight entity) {
		return false;
	}

	@Nonnull
	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntitySpiritBlight entity) {
		return SPIRIT_TEX;
	}
}
