package com.teamwizardry.wizardry.gui.book.pages;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.teamwizardry.wizardry.Utils;
import com.teamwizardry.wizardry.gui.book.Tip;
import com.teamwizardry.wizardry.gui.book.Tippable;
import com.teamwizardry.wizardry.gui.book.indexes.SubIndexElement;
import com.teamwizardry.wizardry.gui.book.util.DataNode;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiPageSubindex extends GuiPageCommon {

    private List<SubIndexElement> elements;
    private int indexPage = 0;

    public GuiPageSubindex(GuiScreen parent, DataNode data, DataNode globalData, String path, int page) {
        super(parent, data, globalData, path, page);

        indexPage = data.get("page").asInt();

        elements = new ArrayList<>();

        List<DataNode> subIndexes = globalData.get("subindex").asList();

        for (DataNode node : subIndexes) {
            String text = node.get("text").asStringOr("ERR - no text");
            String tip = node.get("tip").asString();
            String linkStr = node.get("link").asString();
            int index = linkStr.lastIndexOf(":");
            String link = linkStr.substring(0, index == -1 ? linkStr.length() : index);
            int linkPage = 0;
            if (index != -1) {
                try {
                    linkPage = Integer.parseInt(linkStr.substring(index));
                } catch (NumberFormatException e) {
                }
            }
            SubIndexElement e = null;
            int color = 0xffffffff; // argb
            if (node.get("color").exists()) {
                try {
                    String str = node.get("color").asString();
                    if (str.startsWith("0x"))
                        str = str.substring(2);
                    color = Integer.parseInt(str, 16);
                } catch (NumberFormatException exc) {

                }
            }
            if (node.get("tex").exists()) {
                e = new SubIndexElement(text, link, linkPage, tip, color, new ResourceLocation(node.get("tex").asString()));
            }
            if (node.get("item").exists()) {
                e = new SubIndexElement(text, link, linkPage, tip, color, Item.getByNameOrId(node.get("item").asString()), node.get("damage").asIntOr(0));
            }
            if (e != null)
                elements.add(e);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        mouseX -= viewLeft;
        mouseY -= viewTop;

        int rowHeight = 18;
        int rowCount = 8;
        int x = 5, y = 5;

        for (int i = 0; i < elements.size(); i++) {
            if (i < indexPage * rowCount)
                continue;
            if (i > (indexPage + 1) * rowCount - 1)
                break;
            SubIndexElement element = elements.get(i);

            boolean inside = mouseX >= x && mouseX < x + viewWidth && mouseY >= y && mouseY < y + rowHeight;
            if (inside) {
                this.openPageRelative(element.getLink(), element.getLinkPage());
                break;
            }
            y += rowHeight;
        }
    }

    @Override
    public void drawPage(int mouseX, int mouseY, float partialTicks) {
        int rowHeight = 18;
        int rowCount = 8;
        int x = 5, y = 5;

        for (int i = 0; i < elements.size(); i++) {
            if (i < indexPage * rowCount)
                continue;
            if (i > (indexPage + 1) * rowCount - 1)
                break;
            SubIndexElement element = elements.get(i);

            element.iconGlColor();
            if (element.getTextureType() == SubIndexElement.TextureType.TEXTURE) {
                mc.renderEngine.bindTexture(element.getTextureIcon());
                drawScaledCustomSizeModalRect(x, y, 0, 0, 15, 15, 15, 15, 15, 15);
            } else if (element.getItemIcon() != null) {
                Utils.drawNormalItemStack(new ItemStack(element.getItemIcon(), 1, element.getItemDamage()), x, y);
            } else {
                mc.renderEngine.bindTexture(new ResourceLocation("missingno"));
                drawScaledCustomSizeModalRect(x, y, 0, 0, 15, 15, 15, 15, 15, 15);
            }

            GlStateManager.color(1F, 1F, 1F, 1F);

            boolean inside = mouseX >= x && mouseX < x + viewWidth && mouseY >= y && mouseY < y + rowHeight;
            if (inside) {
                x += 3;
                fontRendererObj.drawString(" | " + ChatFormatting.ITALIC + element.getText().trim(), x + 17, y + fontRendererObj.FONT_HEIGHT / 2, 0);
                if (element.getTip() != null) {
                    int tip = Tippable.setTip(new Tip(element.getTip(), y - 10 + viewTop));
                    if (tip != -1) Tippable.tipManager.put(i, tip);
                }
                x -= 3;
            } else {
                if (Tippable.tipManager.containsKey(i)) {
                    Tippable.removeTip(Tippable.tipManager.get(i));
                    Tippable.tipManager.remove(i);
                }
                fontRendererObj.drawString(" | " + element.getText().trim(), x + 17, y + fontRendererObj.FONT_HEIGHT / 2, 0);
            }

            y += rowHeight;
        }
    }

}
