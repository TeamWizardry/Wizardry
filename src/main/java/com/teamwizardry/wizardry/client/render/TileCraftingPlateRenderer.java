package com.teamwizardry.wizardry.client.render;

import com.teamwizardry.librarianlib.api.PosObject;
import com.teamwizardry.librarianlib.api.util.misc.PosUtils;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
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
            ticker += 1;
            if (ticker > 360) ticker = 0;

            if (te.isCrafting()) {
                // TODO: crafting animations here
            }

            // RENDER INVENTORY ITEMS HERE //
            for (int i = 0; i < te.getInventory().size(); i++) {
                // Get Item
                EntityItem item = new EntityItem(te.getWorld(), x, y, z, te.getInventory().get(i));

                PosObject pos = PosUtils.generateRandomPosition(new PosObject(te.getPos()), 3);

                Minecraft.getMinecraft().getRenderManager().doRenderEntity(item, pos.getX(), pos.getY(), pos.getZ(), ticker, ticker, true);
                GL11.glPopMatrix();
            }
        }
    }
}
