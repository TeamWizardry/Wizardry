package me.lordsaad.wizardry.gui;

import me.lordsaad.wizardry.Wizardry;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Saad on 4/13/2016.
 */
public class MainIndex extends Tippable {

    private boolean didInit = false;
    private HashMap<GuiButton, String> tips = new HashMap<>();
    private HashMap<GuiButton, ResourceLocation> regularTextures = new HashMap<>();
    private HashMap<GuiButton, ResourceLocation> hoverTextures = new HashMap<>();

    @Override
    public void initGui() {
        super.initGui();
        enableNavBar(false);
    }

    private void initIndexButtons() {
        ResourceLocation bulb = new ResourceLocation(Wizardry.MODID, "textures/gui/icons/light-bulb.png");
        ResourceLocation bulb_hover = new ResourceLocation(Wizardry.MODID, "textures/gui/icons/light-bulb-hover.png");
        ResourceLocation laser_blast = new ResourceLocation(Wizardry.MODID, "textures/gui/icons/laser-blast.png");
        ResourceLocation laser_blast_hover = new ResourceLocation(Wizardry.MODID, "textures/gui/icons/laser-blast-hover.png");
        ResourceLocation ringed_beam = new ResourceLocation(Wizardry.MODID, "textures/gui/icons/ringed-beam.png");
        ResourceLocation ringed_beam_hover = new ResourceLocation(Wizardry.MODID, "textures/gui/icons/ringed-beam-hover.png");
        ResourceLocation sun_rad = new ResourceLocation(Wizardry.MODID, "textures/gui/icons/sun-radiations.png");
        ResourceLocation sun_rad_hover = new ResourceLocation(Wizardry.MODID, "textures/gui/icons/sun-radiations-hover.png");

        addButton(new Button(0, left + 25, top + 20, 25, 25), bulb, bulb_hover, "Learn the basics of light manipulation and how everything works.");
        addButton(new Button(1, left + 55, top + 20, 25, 25), laser_blast, laser_blast_hover, "Read about what each item and block in this mod does.");
        addButton(new Button(2, left + 90, top + 20, 25, 25), ringed_beam, ringed_beam_hover, "Learn " +
                "how to accurately manipulate light beams.");
        addButton(new Button(3, left + 25, top + 60, 25, 25), sun_rad, sun_rad_hover, "Learn how to create, transport, manipulate, and store energy.");
        didInit = true;
    }

    public void addButton(Button button, ResourceLocation regularTexture, ResourceLocation hoverTexture, String tip) {
        buttonList.add(button);
        tips.put(button, tip);
        regularTextures.put(button, regularTexture);
        hoverTextures.put(button, hoverTexture);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            mc.thePlayer.openGui(Wizardry.instance, GuiHandler.BASICS, mc.theWorld, (int) mc.thePlayer.posX, (int) mc.thePlayer.posY, (int) mc.thePlayer.posZ);
            clearTips();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (!didInit) initIndexButtons();

        int rowIndex = 0, height = 0, separation = 15, iconSize = 25;
        for (GuiButton button : buttonList) {

            boolean inside = mouseX >= button.xPosition && mouseX < button.xPosition + button.width && mouseY >= button.yPosition && mouseY < button.yPosition + button.height;
            int x = left + separation + (rowIndex * iconSize) + (rowIndex * separation);
            int y = top + separation + (height * iconSize) + (height * separation);

            button.xPosition = x;
            button.yPosition = y;
            button.width = iconSize;
            button.height = iconSize;
            if (inside) {
                ID.put(button, setTip(tips.get(button)));
                mc.renderEngine.bindTexture(hoverTextures.get(button));
            } else {
                if (ID.containsKey(button)) removeTip(ID.get(button));
                mc.renderEngine.bindTexture(regularTextures.get(button));
            }

            drawScaledCustomSizeModalRect(x, y, 0, 0, iconSize, iconSize, iconSize, iconSize, iconSize, iconSize);

            if (rowIndex < 2) rowIndex++;
            else {
                rowIndex = 0;
                height++;
            }
        }

        mc.renderEngine.bindTexture(BACKGROUND_TEXTURE);
        drawTexturedModalRect((width / 2) - 66, (float) (top - 20), 19, 182, 133, 14);
        fontRendererObj.setUnicodeFlag(false);
        fontRendererObj.setBidiFlag(false);
        fontRendererObj.drawString("Physics Book", (width / 2) - 30, (float) (top - 20) + 4, 0, false);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}
