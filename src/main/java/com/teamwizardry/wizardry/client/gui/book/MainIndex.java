package com.teamwizardry.wizardry.client.gui.book;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.util.gui.DataNode;
import com.teamwizardry.wizardry.api.util.gui.PageDataManager;
import com.teamwizardry.wizardry.api.util.gui.PageRegistry;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Saad on 4/13/2016.
 */
public class MainIndex extends Tippable {

    private boolean didInit = false;
    private HashMap<GuiButton, String> tips = new HashMap<>();
    private HashMap<GuiButton, ResourceLocation> icons = new HashMap<>();
    private int iconSize = 25, iconSeparation = 15;

    private List<String> categoryLinks = new ArrayList<>();

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
    }

    /**
     * Initialize all the icons on the front page
     * with the tips from categories.txt
     */
    private void initIndexButtons() {
        int ID = 4, row = 0, column = 0;

        DataNode root = PageDataManager.getPageData("categories");
        List<DataNode> categories = root.asList();

        for (DataNode category : categories) {
            ResourceLocation location = new ResourceLocation(category.get("icon").asStringOr("minecraft:missingno"));
            int x = left + iconSeparation + (row * iconSize) + (row * iconSeparation);
            int y = top + iconSeparation + (column * iconSize) + (column * iconSeparation);
            addNewIndexButton(new Button(++ID, x, y, iconSize, iconSize), location, category.get("text").asString());
            if (row >= 2) {
                row = 0;
                column++;
            } else row++;
            categoryLinks.add(category.get("link").asString());
        }
        didInit = true;
    }

    private void addNewIndexButton(Button button, ResourceLocation regularTexture, String tip) {
        buttonList.add(button);
        tips.put(button, tip);
        icons.put(button, regularTexture);
    }

    /**
     * When the player clicks a button.
     *
     * @param button the button that was clicked.
     * @throws IOException
     */
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id >= 5) {
            mc.displayGuiScreen(PageRegistry.construct(this, categoryLinks.get(button.id - 5), 0));
            didInit = false;
            clearTips();
        }
    }

    /**
     * Render everything in the index
     *
     * @param mouseX       The current position of the mouse on the x axis.
     * @param mouseY       The current position of the mouse on the y axis.
     * @param partialTicks Useless thing.
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        // Initialize if we didn't already.
        if (!didInit) initIndexButtons();

        int row = 0, column = 0;
        for (GuiButton button : buttonList) {
            boolean inside = mouseX >= button.xPosition && mouseX < button.xPosition + button.width && mouseY >= button.yPosition && mouseY < button.yPosition + button.height;
            int x = left + iconSeparation + (row * iconSize) + (row * iconSeparation);
            int y = top + iconSeparation + (column * iconSize) + (column * iconSeparation);

            button.xPosition = x;
            button.yPosition = y;
            button.width = iconSize;
            button.height = iconSize;

            if (icons.containsKey(button)) mc.renderEngine.bindTexture(icons.get(button));
            else mc.renderEngine.bindTexture(new ResourceLocation(Wizardry.MODID, "textures/book/404.png"));

            if (inside) {
                if (tips.containsKey(button)) {
                    int tip;
                    if (column == 1) tip = setTip(new Tip(tips.get(button), (float) (PageBase.top + 50)));
                    else if (column == 2)
                        tip = setTip(new Tip(tips.get(button), (float) (PageBase.bookBackgroundHeight + 20)));
                    else tip = setTip(new Tip(tips.get(button), (float) (top + 10)));

                    if (tip != -1) tipManager.put(button, tip);
                }
                GlStateManager.color(0F, 191F, 255F, 1F);
            } else {
                if (tipManager.containsKey(button)) {
                    removeTip(tipManager.get(button));
                    tipManager.remove(button);
                }
                GlStateManager.color(0F, 0F, 0F, 1F);
            }

            if (row >= 2) {
                row = 0;
                column++;
            } else row++;

            drawScaledCustomSizeModalRect(x, y, 0, 0, iconSize, iconSize, iconSize, iconSize, iconSize, iconSize);
            GlStateManager.color(1F, 1F, 1F, 1F);
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