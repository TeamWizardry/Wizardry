package me.lordsaad.wizardry;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lordsaad.wizardry.book.GuiContentPage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Saad on 4/9/2016.
 */
public class Utils {

    public static void drawSmallItemStack(final ItemStack itemStack, final int x, final int y) {
        if (itemStack != null) {
            GlStateManager.enableRescaleNormal();
            GlStateManager.scale(0.75, 0.75, 0.75);
            RenderHelper.enableGUIStandardItemLighting();
            Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(itemStack, x, y);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.scale(1.0 / 0.75, 1.0 / 0.75, 1.0 / 0.75);
            GlStateManager.disableRescaleNormal();
        }
    }

    public static void drawNormalItemStack(final ItemStack itemStack, final int x, final int y) {
        if (itemStack != null) {
            GlStateManager.enableRescaleNormal();
            RenderHelper.enableGUIStandardItemLighting();
            Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(itemStack, x, y);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
        }
    }

    public static ArrayList<String> padString(String string, int stringSize) {
        ArrayList<String> lines = new ArrayList<>();
        if (string != null)
            for (String line : WordUtils.wrap(string, stringSize).split("\n")) lines.add(line.trim());
        return lines;
    }

    public static HashMap<Integer, ArrayList<String>> splitTextToPages(HashMap<Integer, ArrayList<String>> pages, InputStream stream, GuiContentPage page) {
        List<String> txt = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        try {
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                txt.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int pagenb = 0;
        for (String line : txt) {

            pages.putIfAbsent(pagenb, new ArrayList<>());
            if (pages.get(pagenb).size() >= 18) {
                pagenb++;
                pages.putIfAbsent(pagenb, new ArrayList<>());
            }

            if (line.contains("/n")) pages.get(pagenb).add(" ");

            else if (line.contains("/b")) pages.get(pagenb).add("-----------------------------");

            else if (line.contains("/p")) {
                pagenb++;
                pages.putIfAbsent(pagenb, new ArrayList<>());

            } else {
                if (line.startsWith("*")) {
                    line = line.substring(line.indexOf("*") + 1);
                    ArrayList<String> pads = Utils.padString(line, 30);
                    for (String padded : pads) {
                        if (pages.get(pagenb).size() < 18) {
                            pages.get(pagenb).add(ChatFormatting.ITALIC + padded);
                        } else {
                            pagenb++;
                            pages.putIfAbsent(pagenb, new ArrayList<>());
                            pages.get(pagenb).add(ChatFormatting.ITALIC + padded);
                        }
                    }
                } else {
                    ArrayList<String> pads = Utils.padString(line, 30);
                    for (String padded : pads) {
                        if (pages.get(pagenb).size() < 18) {
                            pages.get(pagenb).add(padded);
                        } else {
                            pagenb++;
                            pages.putIfAbsent(pagenb, new ArrayList<>());
                            pages.get(pagenb).add(padded);
                        }
                    }
                }
            }
        }
        return pages;
    }

    public static void drawConnection(BlockPos pos1, BlockPos pos2, Color color) {
        GlStateManager.pushMatrix();

        GL11.glLineWidth(1);

        GlStateManager.disableTexture2D();
        GlStateManager.color(color.getRed(), color.getGreen(), color.getBlue(), 0.7f);
        GlStateManager.translate(0.5, 0.7, 0.5);

        VertexBuffer vb = Tessellator.getInstance().getBuffer();
        vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        vb.pos(pos2.getX() - pos1.getX(), pos2.getY() - pos1.getY(), pos2.getZ() - pos1.getZ()).endVertex();
        vb.pos(0, 0, 0).endVertex();
        Tessellator.getInstance().draw();

        GlStateManager.enableTexture2D();

        GlStateManager.popMatrix();
    }
}
