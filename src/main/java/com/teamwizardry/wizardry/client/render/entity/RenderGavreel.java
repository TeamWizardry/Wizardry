package com.teamwizardry.wizardry.client.render.entity;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.entity.gods.EntityGavreel;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Saad on 8/21/2016.
 */
public class RenderGavreel extends RenderLiving<EntityGavreel> {

	public static final ResourceLocation SPIRIT_TEX = new ResourceLocation(Wizardry.MODID, "textures/entity/gavreel.png");

	public RenderGavreel(RenderManager renderManager, ModelBase modelBase) {
		super(renderManager, modelBase, 0.0f);
	}

	@Override
	public boolean canRenderName(EntityGavreel entity) {
		return true;
	}

	@NotNull
	@Override
	protected ResourceLocation getEntityTexture(@NotNull EntityGavreel entity) {
		return SPIRIT_TEX;
	}
}
