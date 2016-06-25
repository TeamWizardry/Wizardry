package com.teamwizardry.wizardry.gui.book.pages;

import com.teamwizardry.wizardry.gui.book.util.DataNode;
import com.teamwizardry.wizardry.gui.book.util.TextControl;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuiPageText extends GuiPageCommon {

    private static int LINES_PER_PAGE = 17;

    private List<String> lines;
    private List<TextControl> controls = new ArrayList<>();
    private String text;
    private int pageNum = 0;

    public GuiPageText(GuiScreen parent, DataNode data, DataNode globalData, String path, int page) {
        super(parent, data, globalData, path, page);
        List<DataNode> list = null;
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
                    str += "\n\n"; // if it's two strings then it should have a paragraph break
                }
                String addStr = node.asString();
                addStr = addStr.replaceAll("(?<!\\\\)&([0-9a-fA-Fk-oK-OrR])", "§$1");
                str += addStr;
            }
            if (node.isMap()) {
                TextControl c = new TextControl(node);
                controls.add(c);
                c.text = c.text.replaceAll("(?<!\\\\)&([0-9a-fA-Fk-oK-OrR])", "§$1");

                c.start = str.replaceAll("§.", "").replaceAll("\n", "").length();
                c.end = c.text.replaceAll("§.", "").replaceAll("\n", "").length();

                str += c.text;
            }
        }

        text = str;
    }

    private static boolean isFormatColor(char colorChar) {
        return colorChar >= 48 && colorChar <= 57 || colorChar >= 97 && colorChar <= 102 || colorChar >= 65 && colorChar <= 70;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        mouseX -= viewLeft;
        mouseY -= viewTop;
        for (TextControl control : controls) {
            if (control.isHovering(mouseX, mouseY)) {
                control.mouseClick(this, mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    public void drawPage(int mouseX, int mouseY, float partialTicks) {
        int leftOffset = 2;
        FontRenderer fr = mc.fontRendererObj;
        if (lines == null) {
            this.lines = new ArrayList<>();
            List<String> lines = listFormattedStringToWidthKeepTrailingWhitespace(text, viewWidth - leftOffset);
            int y = 0;
            int index = 0;
            boolean leadingWhitespace = true;
            for (TextControl control : controls) {
                control.rects = new ArrayList<>();
            }

            for (int i = 0; i < lines.size(); i++) {
                if (i >= (pageNum + 1) * LINES_PER_PAGE)
                    break;
                String line = lines.get(i);
                line = line.replaceAll("§.", "");
                if (i >= pageNum * LINES_PER_PAGE && !(leadingWhitespace && line.matches("^\\s*$"))) {
                    leadingWhitespace = false;
                    this.lines.add(lines.get(i));

                    int is = index;
                    int ie = index + line.length();
                    for (TextControl control : controls) {

                        int s = control.start;
                        int e = s + control.end;


                        int boxStart = 0, boxEnd = 0;
                        boolean isStartInside = false, isEndInside = false, shouldAddBox = false;

                        isStartInside = (is <= s && s <= ie);
                        isEndInside = (is <= e && e <= ie);

                        if (s <= is && e >= ie) { // full line
                            shouldAddBox = true;
                            boxStart = 0;
                            boxEnd = line.length();
                        } else if (isStartInside && isEndInside) { // part of the line
                            shouldAddBox = true;
                            boxStart = s - index;
                            boxEnd = e - index;
                        } else if (isStartInside) { // last part of line
                            shouldAddBox = true;
                            boxStart = s - index;
                            boxEnd = line.length();
                        } else if (isEndInside) { // first part of line
                            shouldAddBox = true;
                            boxStart = 0;
                            boxEnd = e - index;
                        }

                        if (shouldAddBox) {
                            int left = fr.getStringWidth(line.substring(0, boxStart));
                            int right = fr.getStringWidth(line.substring(0, boxEnd));
                            control.rects.add(new Rectangle(left + leftOffset, y, right - left, fr.FONT_HEIGHT));
                        }

                        if (i == lines.size() - 1) {
                            boxStart = 1;
                        }
                    }
                    y += fr.FONT_HEIGHT;
                }
                index += line.length();
            }
        }

        int color = 0x000000;
        int y = 0;
        for (int i = 0; i < lines.size(); i++) {
            fr.drawString(lines.get(i), leftOffset, y, color);
            y += fr.FONT_HEIGHT;
        }

        for (TextControl control : controls) {
            control.draw(this, mouseX, mouseY, partialTicks);
        }
    }

    public List<String> listFormattedStringToWidthKeepTrailingWhitespace(String str, int wrapWidth) {
        return Arrays.asList(this.wrapFormattedStringToWidth(str, wrapWidth).split("\n"));
    }

    /**
     * Inserts newline and formatting into a string to wrap it within the specified width.
     */
    String wrapFormattedStringToWidth(String str, int wrapWidth) {
        int i = this.sizeStringToWidth(str, wrapWidth);

        if (str.length() <= i) {
            return str;
        } else {
            String s = str.substring(0, i);
            char c0 = str.charAt(i);
            boolean flag = c0 == 32 || c0 == 10;
            String s1 = FontRenderer.getFormatFromString(s) + str.substring(i + (flag ? 1 : 0));
            return s + (c0 == 32 ? " " : "") + "\n" + this.wrapFormattedStringToWidth(s1, wrapWidth);
        }
    }

    /**
     * Determines how many characters from the string will fit into the specified width.
     */
    private int sizeStringToWidth(String str, int wrapWidth) {
        int i = str.length();
        int j = 0;
        int k = 0;
        int l = -1;

        for (boolean flag = false; k < i; ++k) {
            char c0 = str.charAt(k);

            switch (c0) {
                case '\n':
                    --k;
                    break;
                case ' ':
                    l = k;
                default:
                    j += mc.fontRendererObj.getCharWidth(c0);

                    if (flag) {
                        ++j;
                    }

                    break;
                case '\u00a7':

                    if (k < i - 1) {
                        ++k;
                        char c1 = str.charAt(k);

                        if (c1 != 108 && c1 != 76) {
                            if (c1 == 114 || c1 == 82 || isFormatColor(c1)) {
                                flag = false;
                            }
                        } else {
                            flag = true;
                        }
                    }
            }

            if (c0 == 10) {
                ++k;
                l = k;
                break;
            }

            if (j > wrapWidth) {
                break;
            }
        }

        return k != i && l != -1 && l < k ? l : k;
    }
}
