package com.teamwizardry.wizardry.client.render;

import com.teamwizardry.librarianlib.client.core.ClientTickHandler;
import com.teamwizardry.wizardry.common.tile.TileStaff;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

/**
 * Created by Saad on 5/7/2016.
 */
public class TileStaffRenderer extends TileEntitySpecialRenderer<TileStaff> {

	@Override
	public void renderTileEntityAt(TileStaff te, double x, double y, double z, float partialTicks, int destroyStage) {
		if (te.pearl != null) {
			double time = ClientTickHandler.getTicksInGame() + partialTicks;

			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z);
			GlStateManager.translate(0.5, 1.35, 0.5);
			GlStateManager.scale(0.5, 0.5, 0.5);
			GlStateManager.rotate((float) time, 0, 1, 0);
			Minecraft.getMinecraft().getRenderItem().renderItem(te.pearl, ItemCameraTransforms.TransformType.NONE);
			GlStateManager.popMatrix();
		}
	}
}
