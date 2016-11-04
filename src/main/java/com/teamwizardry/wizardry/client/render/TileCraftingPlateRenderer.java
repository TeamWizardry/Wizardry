package com.teamwizardry.wizardry.client.render;

import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

/**
 * Created by Saad on 6/11/2016.
 */
public class TileCraftingPlateRenderer extends TileEntitySpecialRenderer<TileCraftingPlate> {

	@Override
	public void renderTileEntityAt(TileCraftingPlate te, double x, double y, double z, float partialTicks, int destroyStage) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5, y + 1, z + 0.5);
		GlStateManager.scale(0.4, 0.4, 0.4);
		GlStateManager.rotate(te.tick, 0, 1, 0);
		Minecraft.getMinecraft().getRenderItem().renderItem(te.output.getStackInSlot(0), TransformType.NONE);
		GlStateManager.popMatrix();

	}
}
