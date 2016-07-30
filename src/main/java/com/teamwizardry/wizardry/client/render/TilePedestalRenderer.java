package com.teamwizardry.wizardry.client.render;

import com.teamwizardry.wizardry.common.tile.TilePedestal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

/**
 * Created by Saad on 5/7/2016.
 */
public class TilePedestalRenderer extends TileEntitySpecialRenderer<TilePedestal> {
    private int ticker = 0;

    @Override
    public void renderTileEntityAt(TilePedestal te, double x, double y, double z, float partialTicks, int destroyStage) {
        if (te.getStack() != null) {

            ticker += 2 * partialTicks;
            if (ticker > 360) ticker = 0;

            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5, y + 1.2, z + 0.5);
            GlStateManager.scale(0.4, 0.4, 0.4);
            GlStateManager.rotate(ticker, 0, 1, 0);
            Minecraft.getMinecraft().getRenderItem().renderItem(te.getStack(), ItemCameraTransforms.TransformType.NONE);
            GlStateManager.popMatrix();
        }
    }
}
