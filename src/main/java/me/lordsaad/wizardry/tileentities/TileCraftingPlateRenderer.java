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
        ticker += 2;
        if (ticker > 360) ticker = 0;

        for (int i = 0; i < te.getInventory().size(); i++) {
            GL11.glPushMatrix();
            EntityItem item = new EntityItem(te.getWorld(), x, y, z, te.getInventory().get(i));
            item.hoverStart = 0;
            double shifted = ticker + i * (360.0 / te.getInventory().size());
            GL11.glTranslated(x + 0.5, y + 0.6, z + 0.5);
            GL11.glRotated(shifted, 0, 1, 0);
            GL11.glTranslated(-0.5, 0, 0);
            GL11.glRotated(shifted, 0, 1, 0);
            Minecraft.getMinecraft().getRenderManager().doRenderEntity(item, 0, 0, 0, 0, 0, true);
            GL11.glPopMatrix();
        }
    }
}
