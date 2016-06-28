package com.teamwizardry.wizardry.tileentities;

import com.teamwizardry.wizardry.ModItems;
import com.teamwizardry.wizardry.items.pearls.ItemQuartzPearl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

/**
 * Created by Saad on 6/11/2016.
 */
public class TileCraftingPlateRenderer extends TileEntitySpecialRenderer<TileCraftingPlate> {

    private int ticker = 0, craftingTime = -1, craftingProgress = -1;
    private boolean isCrafting = false, finishedCrafting = false, end = false;
    private ItemStack pearl;
    private double raise = 0;

    @Override
    public void renderTileEntityAt(TileCraftingPlate te, double x, double y, double z, float partialTicks, int destroyStage) {
        if (te.isStructureComplete()) {
            ticker += 2;
            if (ticker > 360) ticker = 0;

            if (isCrafting) {
                raise += 0.1;
                if (craftingTime == -1) craftingTime = (te.getInventory().size() - 1) * 100;

                if (!finishedCrafting)
                    if (craftingProgress < craftingTime)
                        craftingProgress++;
                if (end) {
                    craftingProgress = 0;
                    isCrafting = false;
                    finishedCrafting = true;
                    craftingTime = -1;
                    raise = 0;
                }
            }

            // LOOP //
            for (int i = 0; i < te.getInventory().size(); i++) {
                // Get Item
                EntityItem item = new EntityItem(te.getWorld(), x, y, z, te.getInventory().get(i));
                if (item.getEntityItem().getItem() == ModItems.PEARL_QUARTZ) pearl = item.getEntityItem();

                item.hoverStart = 0;
                double shifted;
                if (isCrafting) shifted = ticker + i * (360.0 / (te.getInventory().size() - 1));
                else shifted = ticker + i * (360.0 / te.getInventory().size());

                GL11.glPushMatrix();

                if (item.getEntityItem() == pearl) {
                    GL11.glTranslated(x + 0.5, y + 0.3, z + 0.5);
                    if (!isCrafting) {
                        // IS NOT CRAFTING //
                        finishedCrafting = false;
                        isCrafting = true;
                    } else {
                        // IS CRAFTING //

                        // Raise the PEARL_QUARTZ slowly when crafting
                        if (raise < 70) GL11.glTranslated(0, raise / 100, 0);
                        else GL11.glTranslated(0, 70, 0);

                        // Rotate the PEARL_QUARTZ faster as it crafts
                        GL11.glRotated(shifted + raise * 500, 0, 1, 0);
                    }

                    if (finishedCrafting)
                        ((ItemQuartzPearl) pearl.getItem()).addSpellItems(item.getEntityItem(), te.getInventory());

                    // Position PEARL_QUARTZ lower than the rest of the items.

                } else {
                    // NOT A PEARL //

                    // Position item higher than the PEARL_QUARTZ.
                    GL11.glTranslated(x + 0.5, y + 0.6, z + 0.5);

                    // Rotate item around center
                    if (!isCrafting) GL11.glRotated(shifted, 0, 1, 0);
                    else GL11.glRotated(shifted + raise, 0, 1, 0);

                    // Radius of the items around the PEARL_QUARTZ.
                    if (!finishedCrafting) GL11.glTranslated(-0.5, 0, 0);
                    else GL11.glTranslated(-0.5 + raise / 100, 0, 0);
                    // Rotate the item around itself
                    GL11.glRotated(shifted, 0, 1, 0);
                }

                Minecraft.getMinecraft().getRenderManager().doRenderEntity(item, 0, 0, 0, 0, 0, true);
                GL11.glPopMatrix();
            }
        }
    }
}
