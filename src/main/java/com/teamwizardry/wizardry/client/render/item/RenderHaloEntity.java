package com.teamwizardry.wizardry.client.render.item;

import com.teamwizardry.wizardry.api.item.BaublesSupport;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RenderHaloEntity implements LayerRenderer<EntityLivingBase> {

	private ModelRenderer modelRenderer;

	public RenderHaloEntity(ModelRenderer modelRenderer) {
		this.modelRenderer = modelRenderer;
	}

	@Override
	public void doRenderLayer(@NotNull EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		if (BaublesSupport.getItem(entitylivingbaseIn, ModItems.HALO).isEmpty()) return;
		ItemStack halo = BaublesSupport.getItem(entitylivingbaseIn, ModItems.HALO);

		GlStateManager.pushMatrix();

		if (entitylivingbaseIn.isSneaking()) GlStateManager.translate(0.0f, 0.2f, 0.0f);

		boolean flag = entitylivingbaseIn instanceof EntityVillager || entitylivingbaseIn instanceof EntityZombieVillager;

		if (entitylivingbaseIn.isChild() && !(entitylivingbaseIn instanceof EntityVillager)) {
			GlStateManager.translate(0.0f, 0.5f * scale, 0.0f);
			GlStateManager.scale(0.7f, 0.7f, 0.7f);
			GlStateManager.translate(0.0f, 16.0f * scale, 0.0f);
		}

		if (flag) GlStateManager.translate(0.0f, 0.1875f, 0.0f);

		this.modelRenderer.postRender(0.0625f);
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

		GlStateManager.translate(0.0f, -0.25f, 0.0f);
		GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
		GlStateManager.scale(0.625f, -0.625f, -0.625f);

		Minecraft.getMinecraft().getItemRenderer().renderItem(entitylivingbaseIn, halo, ItemCameraTransforms.TransformType.HEAD);

		GlStateManager.popMatrix();

	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}
