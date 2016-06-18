package me.lordsaad.wizardry.gui.book.util;

import net.minecraft.client.renderer.GlStateManager;

public class Color {

    public float r, g, b, a;

    public Color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = 1;
    }

    public static Color argb(int color) {
        float a = ((color >> 24) & 0xff) / 255f;
        float r = ((color >> 16) & 0xff) / 255f;
        float g = ((color >> 8) & 0xff) / 255f;
        float b = ((color >> 0) & 0xff) / 255f;
        return new Color(r, g, b, a);
    }

    public static Color rgba(int color) {
        float r = ((color >> 24) & 0xff) / 255f;
        float g = ((color >> 16) & 0xff) / 255f;
        float b = ((color >> 8) & 0xff) / 255f;
        float a = ((color >> 0) & 0xff) / 255f;
        return new Color(r, g, b, a);
    }

    public static Color rgb(int color) {
        float r = ((color >> 16) & 0xff) / 255f;
        float g = ((color >> 8) & 0xff) / 255f;
        float b = ((color >> 0) & 0xff) / 255f;
        return new Color(r, g, b);
    }

    public void glColor() {
        GlStateManager.color(r, g, b, a);
    }

}
