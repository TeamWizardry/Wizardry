package me.lordsaad.wizardry.gui;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lordsaad.wizardry.Wizardry;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Saad on 5/2/2016.
 */
public class GuiSubIndex extends GuiContentPage {

    protected ArrayList<IndexItem> indexItems;

    @Override
    public void initGui() {
        super.initGui();
        indexItems = new ArrayList<>();
        hoverTextures.clear();
        regularTextures.clear();
        recipes.clear();
        pages.clear();
        enableNavBar(true);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0: {
                if (currentPage > 0) {
                    currentPage--;
                    mc.thePlayer.openGui(Wizardry.instance, pageID, mc.theWorld, (int) mc.thePlayer.posX, (int)
                            mc.thePlayer.posY, (int) mc.thePlayer.posZ);
                } else {
                    mc.thePlayer.openGui(Wizardry.instance, GuiHandler.INDEX, mc.theWorld, (int) mc.thePlayer.posX, (int)
                            mc.thePlayer.posY, (int) mc.thePlayer.posZ);
                    currentPage = 0;
                }
                break;
            }
            case 1: {
                if (pages.size() > currentPage) {
                    currentPage++;
                    mc.thePlayer.openGui(Wizardry.instance, pageID, mc.theWorld, (int) mc.thePlayer.posX, (int)
                            mc.thePlayer.posY, (int) mc.thePlayer.posZ);
                }
                break;
            }
            case 2: {
                mc.thePlayer.openGui(Wizardry.instance, GuiHandler.INDEX, mc.theWorld, (int) mc.thePlayer.posX, (int)
                        mc.thePlayer.posY, (int) mc.thePlayer.posZ);
                currentPage = 0;
                break;
            }
            default: {
                indexItems.stream().filter(item -> item.getButton() == button).forEach(item -> mc.thePlayer.openGui(Wizardry.instance, item.getPageID(), mc.theWorld, (int) mc.thePlayer.posX, (int) mc.thePlayer.posY, (int) mc.thePlayer.posZ));
                break;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color(1F, 1F, 1F, 1F);
        mc.renderEngine.bindTexture(BACKGROUND_TEXTURE);
        drawTexturedModalRect(left, top, 0, 0, guiWidth, guiHeight);

        fontRendererObj.setUnicodeFlag(true);
        fontRendererObj.setBidiFlag(true);

        for (IndexItem item : indexItems) {
            GlStateManager.color(1F, 1F, 1F, 1F);
            int x = left + 20, y = top + 10 + ((item.getIndexID() - 4) * 15);
            mc.renderEngine.bindTexture(item.getIcon());

            item.getButton().xPosition = x;
            item.getButton().yPosition = y + 2;
            item.getButton().width = guiWidth;
            item.getButton().drawButton(mc, x, y);
            drawScaledCustomSizeModalRect(x, y, 0, 0, 15, 15, 15, 15, 15, 15);

            boolean inside = mouseX >= item.getButton().xPosition && mouseX < item.getButton().xPosition + item.getButton().width && mouseY >= item.getButton().yPosition && mouseY < item.getButton().yPosition + item.getButton().height;
            if (inside) {
                x += 3;
                fontRendererObj.drawString(" | " + ChatFormatting.ITALIC + item.getText().trim(), x + 17, y + fontRendererObj.FONT_HEIGHT / 2, 0);
                //     if (!ID.containsKey(item.getButton())) ID.put(item.getButton(), setTip(item.getTip().trim()));
            } else {
                //   if (ID.containsKey(item.getButton())) {
                //      removeTip(ID.get(item.getButton()));
                //      ID.remove(item.getButton());
                //    }
                fontRendererObj.drawString(" | " + item.getText().trim(), x + 17, y + fontRendererObj.FONT_HEIGHT / 2, 0);
            }
        }

        GlStateManager.color(1F, 1F, 1F, 1F);
        mc.renderEngine.bindTexture(BACKGROUND_TEXTURE);
        drawTexturedModalRect((width / 2) - 66, (float) (top - 20), 19, 182, 133, 14);
        fontRendererObj.setUnicodeFlag(false);
        fontRendererObj.setBidiFlag(false);
        fontRendererObj.drawString("Physics Book", (width / 2) - 30, (float) (top - 20) + 4, 0, false);
    }
}
