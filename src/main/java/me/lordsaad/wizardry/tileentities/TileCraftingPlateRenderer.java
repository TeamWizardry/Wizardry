package me.lordsaad.wizardry.tileentities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import org.lwjgl.opengl.GL11;

/**
 * Created by Saad on 6/11/2016.
 */
public class TileCraftingPlateRenderer extends TileEntitySpecialRenderer<TileCraftingPlate> {

    private int ticker = 0;

    @Override
    public void renderTileEntityAt(TileCraftingPlate te, double x, double y, double z, float partialTicks, int destroyStage) {
        if (te.isStructureComplete()) {
            ticker += 2;
            if (ticker > 360) ticker = 0;

            for (int i = 0; i < te.getInventory().size(); i++) {

                int pearlRotationMultiplier = 1;
                if (te.getPearl() != null) {
                    if (te.isCrafting()) {
                        pearlRotationMultiplier = te.getCraftingProgress() / 10;
                    } else {
                        // Not crafting. Act natural.

                    }
                }

                // Check if pearl
                EntityItem item = new EntityItem(te.getWorld(), x, y, z, te.getInventory().get(i));

                item.hoverStart = 0;
                double shifted = ticker + i * (360.0 / te.getInventory().size());
                GL11.glPushMatrix();

                // Position the pearl lower than the rest of the items.
                if (item.getEntityItem() != te.getPearl()) GL11.glTranslated(x + 0.5, y + 0.6, z + 0.5);
                else GL11.glTranslated(x + 0.5, y + 0.3, z + 0.5);

                // Rotate items around center
                if (item.getEntityItem() != te.getPearl()) GL11.glRotated(shifted, 0, 1, 0);

                // Radius of the items around the pearl.
                // Raise the pearl slowly when crafting
                if (item.getEntityItem() != te.getPearl()) GL11.glTranslated(-0.5, 0, 0);
                else if (te.getCraftingProgress() - te.getCraftingTime() <= 50)
                    GL11.glTranslated(0, (te.getCraftingProgress() - te.getCraftingTime()) / 15, 0);

                // Rotate the pearl faster depending on the pearlRotationMultiplier
                // which is based on if it's crafting or not.
                if (item.getEntityItem() != te.getPearl()) GL11.glRotated(shifted, 0, 1, 0);
                else GL11.glRotated(shifted * pearlRotationMultiplier, 0, 1, 0);

                Minecraft.getMinecraft().getRenderManager().doRenderEntity(item, 0, 0, 0, 0, 0, true);
                GL11.glPopMatrix();

            }
        }
    }
}
