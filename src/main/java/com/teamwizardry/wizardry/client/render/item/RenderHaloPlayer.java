package com.teamwizardry.wizardry.client.render.item;

import com.teamwizardry.wizardry.api.item.BaublesSupport;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RenderHaloPlayer implements LayerRenderer<EntityPlayer> {

	private ModelRenderer modelRenderer;

	public RenderHaloPlayer(ModelRenderer modelRenderer) {
		this.modelRenderer = modelRenderer;
	}

	@Override
	public void doRenderLayer(@NotNull EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		if (BaublesSupport.getItem(player, ModItems.HALO).isEmpty()) return;
		ItemStack halo = BaublesSupport.getItem(player, ModItems.HALO);

		GlStateManager.pushMatrix();

		if (player.isSneaking()) GlStateManager.translate(0.0f, 0.2f, 0.0f);

		this.modelRenderer.postRender(0.0625f);
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

		GlStateManager.translate(0.0f, -0.25f, 0.0f);
		GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
		GlStateManager.scale(0.625f, -0.625f, -0.625f);

		Minecraft.getMinecraft().getItemRenderer().renderItem(player, halo, ItemCameraTransforms.TransformType.HEAD);

		GlStateManager.popMatrix();
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}
