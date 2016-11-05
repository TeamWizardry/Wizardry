package com.teamwizardry.wizardry.client.render;

import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate.ClusterObject;
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

		for (ClusterObject cluster : te.inventory) {
			if (cluster.reverse) {
				if (cluster.tick < 360) cluster.tick += 1 * cluster.speedMultiplier;
				else cluster.tick = 0;
			} else if (cluster.tick > 0) cluster.tick -= 1 * cluster.speedMultiplier;
			else cluster.tick = 360;
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + cluster.dest.xCoord, y + cluster.dest.yCoord, z + cluster.dest.zCoord);
			GlStateManager.scale(0.4, 0.4, 0.4);
			GlStateManager.rotate(cluster.tick, 0, 1, 0);
			Minecraft.getMinecraft().getRenderItem().renderItem(cluster.stack, TransformType.NONE);
			GlStateManager.popMatrix();
		}

		if (te.output != null) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5, y + 1, z + 0.5);
			GlStateManager.scale(0.4, 0.4, 0.4);
			GlStateManager.rotate(te.tick, 0, 1, 0);
			Minecraft.getMinecraft().getRenderItem().renderItem(te.output, TransformType.NONE);
			GlStateManager.popMatrix();
		}
	}

}
