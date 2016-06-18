package me.lordsaad.wizardry.gui.book.pages;

import me.lordsaad.wizardry.gui.book.util.DataNode;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;

public class GuiPageText extends GuiPageCommon {

    private static int LINES_PER_PAGE = 17;

    private List<String> lines;
    private String text;
    private int pageNum = 0;

    public GuiPageText(GuiScreen parent, DataNode data, DataNode globalData, String path, int page) {
        super(parent, data, globalData, path, page);
        List<DataNode> list;
        if (data.get("text").isList()) {
            list = data.get("text").asList();
        } else {
            DataNode texts = globalData.get("texts");
            list = texts.get(data.get("text").get("global").asString()).asList();
            pageNum = data.get("text").get("page").asInt();
        }
        String str = "";
        for (int i = 0; i < list.size(); i++) {
            DataNode node = list.get(i);
            if (node.isString()) {
                if (i != 0 && list.get(i - 1).isString()) {
                    str += "§0§r\n"; // if it's two strings then it should have a linebreak
                }
                str += node.asString();
            }
            if (node.isMap()) {
                str += node.get("text").asStringOr("");
            }
        }
        str.replaceAll("(?<!\\\\)&([0-9a-fA-Fk-oK-OrR])", "§$1");

        text = str;
    }

    @Override
    public void drawPage(int mouseX, int mouseY, float partialTicks) {
        if (lines == null) {
            lines = mc.fontRendererObj.listFormattedStringToWidth(text, viewWidth);
        }

        FontRenderer fr = mc.fontRendererObj;
        int color = 0x000000;
        int y = 0;
        for (int i = 0; i < LINES_PER_PAGE && i + pageNum * LINES_PER_PAGE < lines.size(); i++) {
            fr.drawString(lines.get(i + pageNum * LINES_PER_PAGE), 0, y, color);
            y += fr.FONT_HEIGHT;
        }

    }

}
