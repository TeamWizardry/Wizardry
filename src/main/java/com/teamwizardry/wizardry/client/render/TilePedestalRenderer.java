package com.teamwizardry.wizardry.client.render;

import com.teamwizardry.wizardry.common.tile.TilePedestal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.lwjgl.opengl.GL11;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Saad on 5/7/2016.
 */
public class TilePedestalRenderer extends TileEntitySpecialRenderer<TilePedestal> {
    private int ticker = 0;

    @Override
    public void renderTileEntityAt(TilePedestal te, double x, double y, double z, float partialTicks, int destroyStage) {
        if (te.getPearl() != null) {

            ticker += 2 * partialTicks;
            if (ticker > 360) ticker = 0;

            GL11.glPushMatrix();
            GL11.glTranslated(x + 0.5, y + 1.5, z + 0.5);
            GL11.glScaled(0.4, 0.4, 0.4);
            if (ThreadLocalRandom.current().nextBoolean()) {
                if (ThreadLocalRandom.current().nextBoolean())
                    if (ThreadLocalRandom.current().nextBoolean()) GL11.glRotated(ticker, 0, 1, 1);
                    else GL11.glRotated(ticker, 0, 0, 1);
                else GL11.glRotated(ticker, 1, 1, 1);
            } else {
                if (ThreadLocalRandom.current().nextBoolean())
                    if (ThreadLocalRandom.current().nextBoolean()) GL11.glRotated(ticker, 1, 1, 0);
                    else GL11.glRotated(ticker, 1, 0, 0);
                else GL11.glRotated(ticker, 1, 0, 1);
            }

            Minecraft.getMinecraft().getRenderItem().renderItem(te.getPearl(), ItemCameraTransforms.TransformType.FIXED);
            GL11.glPopMatrix();
        }
    }
}
