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
    private double rotationTicker = 0;

    @Override
    public void renderTileEntityAt(TileCraftingPlate te, double x, double y, double z, float partialTicks, int destroyStage) {
        if (te.isStructureComplete()) {
            ticker += 1;
            if (ticker > 360) ticker = 0;

            // RENDER INVENTORY ITEMS HERE WHEN CRAFTING //
            if (te.isCrafting()) {
                if (te.getCraftingTimeLeft() > 0.0) {
                    rotationTicker += te.getCraftingTime() / te.getCraftingTimeLeft();
                    if (rotationTicker > 360) rotationTicker = 0;
                }
                for (CraftingPlateItemStackHelper stack : te.getInventory()) {

                    if (te.getCraftingTimeLeft() > 0.0) {
                        if (stack.getPositionTheta() >= 1 || stack.getPositionTheta() + 1.0 / te.getCraftingTimeLeft() > 1)
                            stack.setPositionTheta(1.0 / te.getCraftingTime());
                        else stack.setPositionTheta(stack.getPositionTheta() + 1.0 / te.getCraftingTimeLeft());

                        stack.setMaxX(stack.getMaxX() - (stack.getMaxX() / te.getCraftingTimeLeft() / 4));
                        stack.setMaxZ(stack.getMaxZ() - (stack.getMaxZ() / te.getCraftingTimeLeft() / 4));
                        stack.setMaxY(stack.getMaxY() - (stack.getMaxY() / te.getCraftingTimeLeft() / 4));
                    }

                    double theta = Math.PI * 2 * stack.getPositionTheta();
                    double cosTheta = Math.cos(theta);
                    double sinTheta = Math.sin(theta);
                    stack.setPoint(new Vec3d(cosTheta * stack.getMaxX(), stack.getMaxY(), sinTheta * stack.getMaxZ()));

                    GlStateManager.pushMatrix();
                    GlStateManager.translate(x + stack.getPoint().xCoord + 0.5, y + stack.getPoint().yCoord + 1, z + stack.getPoint().zCoord + 0.5);
                    GlStateManager.scale(0.4, 0.4, 0.4);
                    GlStateManager.rotate((float) rotationTicker, 0, 1, 0);
                    Minecraft.getMinecraft().getRenderItem().renderItem(stack.getItemStack(), ItemCameraTransforms.TransformType.NONE);
                    GlStateManager.popMatrix();
                }
            }

            // RENDER INVENTORY ITEMS HERE WHEN NOT CRAFTING //
            if (!te.isCrafting())
                for (CraftingPlateItemStackHelper stack : te.getInventory()) {

                    if (stack.getQueue() < stack.getPoints().size() - 1) stack.setQueue(stack.getQueue() + 1);
                    Vec3d point = stack.getPoints().get(stack.getQueue());

                    GlStateManager.pushMatrix();
                    GlStateManager.translate(x + point.xCoord + 0.5, y + point.yCoord + 0.5, z + point.zCoord + 0.5);
                    GlStateManager.scale(0.4, 0.4, 0.4);
                    GlStateManager.rotate((float) (ticker * (stack.getMaxX() + stack.getMaxZ()) / 2), 0, 1, 0);
                    Minecraft.getMinecraft().getRenderItem().renderItem(stack.getItemStack(), ItemCameraTransforms.TransformType.NONE);
                    GlStateManager.popMatrix();
                }
        }
    }
}
