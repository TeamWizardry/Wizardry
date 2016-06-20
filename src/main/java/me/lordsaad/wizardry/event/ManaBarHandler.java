package me.lordsaad.wizardry.event;

import me.lordsaad.wizardry.Wizardry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by Saad on 6/20/2016.
 */
public class ManaBarHandler extends Gui {

    private static final ResourceLocation texture = new ResourceLocation(Wizardry.MODID, "textures/gui/book/error/error.png");
    private final static int BAR_WIDTH = 81;
    private final static int BAR_HEIGHT = 9;
    private final static int BAR_SPACING_ABOVE_EXP_BAR = 3;
    private Minecraft mc;

    public ManaBarHandler(Minecraft mc) {
        this.mc = mc;
    }

    public void renderManaBar(int screenWidth, int screenHeight) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(texture);
        mc.thePlayer.sendChatMessage(".");
        final int vanillaExpLeftX = screenWidth / 2 - 91; // leftmost edge of the experience bar
        final int vanillaExpTopY = screenHeight - 32 + 3;  // top of the experience bar
        GL11.glTranslatef(vanillaExpLeftX, vanillaExpTopY - BAR_SPACING_ABOVE_EXP_BAR - BAR_HEIGHT, 0);

        drawTexturedModalRect(0, 0, 0, 0, BAR_WIDTH, BAR_HEIGHT);
        GL11.glTranslatef(1, 1, 0);

        GL11.glPushMatrix();
        GL11.glPopAttrib();

    }

}
