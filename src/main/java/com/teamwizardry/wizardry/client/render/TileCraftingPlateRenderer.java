package com.teamwizardry.wizardry.client.render;

import com.teamwizardry.wizardry.client.helper.CraftingPlateItemStackHelper;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.Vec3d;

/**
 * Created by Saad on 6/11/2016.
 */
public class TileCraftingPlateRenderer extends TileEntitySpecialRenderer<TileCraftingPlate> {

    private int ticker = 0;

    @Override
    public void renderTileEntityAt(TileCraftingPlate te, double x, double y, double z, float partialTicks, int destroyStage) {
        if (te.isStructureComplete()) {
            ticker += 1;
            if (ticker > 360) ticker = 0;

            if (te.isCrafting()) {
                // TODO: crafting animations here
            }

            // RENDER INVENTORY ITEMS HERE //
            for (int i = 0; i < te.getInventory().size(); i++) {
                CraftingPlateItemStackHelper stack = te.getInventory().get(i);

                if (stack.getQueue() < stack.getPoints().size() - 1) stack.setQueue(stack.getQueue() + 1);
                Vec3d point = stack.getPoints().get(stack.getQueue());

                GlStateManager.pushMatrix();
                GlStateManager.translate(point.xCoord, point.yCoord, point.zCoord);
                GlStateManager.scale(0.4, 0.4, 0.4);
                GlStateManager.rotate(ticker, 0, 1, 0);
                Minecraft.getMinecraft().getRenderItem().renderItem(stack.getItemStack(), ItemCameraTransforms.TransformType.NONE);
                GlStateManager.popMatrix();

            }
        }
    }
}
