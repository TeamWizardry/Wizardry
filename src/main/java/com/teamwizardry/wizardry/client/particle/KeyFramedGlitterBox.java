package com.teamwizardry.wizardry.client.particle;

import com.teamwizardry.librarianlib.math.Easing;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public class KeyFramedGlitterBox {

    public final int lifetime;

    public int posFrameCount = 0;
    public double[] pos = new double[3 * 5];
    public double[] posEasings = new double[]{0, 0, 0, 0, 0};

    public int colorFrameCount = 0;
    public double[] color = new double[5 * 4];
    public double[] colorEasings = new double[]{0, 0, 0, 0, 0};

    public int sizeFrameCount = 0;
    public double[] size = new double[5];
    public double[] sizeEasings = new double[]{0, 0, 0, 0, 0};

    public int alphaFrameCount = 0;
    public double[] alpha = new double[5];
    public double[] alphaEasings = new double[]{0, 0, 0, 0, 0};

    public KeyFramedGlitterBox(int lifetime) {
        this.lifetime = lifetime;
    }

    public KeyFramedGlitterBox pos(Easing easing, double x, double y, double z) {
        posEasings[posFrameCount] = KeyFramedGlitter.easingArray.indexOf(easing);
        pos[posFrameCount * 3] = x;
        pos[posFrameCount * 3 + 1] = y;
        pos[posFrameCount * 3 + 2] = z;
        posFrameCount++;
        return this;
    }

    public KeyFramedGlitterBox pos(Easing easing, Vec3d vec) {
        posEasings[posFrameCount] = KeyFramedGlitter.easingArray.indexOf(easing);
        pos[posFrameCount * 3] = vec.x;
        pos[posFrameCount * 3 + 1] = vec.y;
        pos[posFrameCount * 3 + 2] = vec.z;
        posFrameCount++;
        return this;
    }

    public KeyFramedGlitterBox color(Easing easing, double r, double g, double b) {
        colorEasings[colorFrameCount] = KeyFramedGlitter.easingArray.indexOf(easing);
        color[colorFrameCount * 4] = r;
        color[colorFrameCount * 4 + 1] = g;
        color[colorFrameCount * 4 + 2] = b;
        color[colorFrameCount * 4 + 3] = 1;
        colorFrameCount++;
        return this;
    }

    public KeyFramedGlitterBox color(Easing easing, Color value) {
        colorEasings[colorFrameCount] = KeyFramedGlitter.easingArray.indexOf(easing);
        color[colorFrameCount * 3] = value.getRed() / 255.0;
        color[colorFrameCount * 3 + 1] = value.getGreen() / 255.0;
        color[colorFrameCount * 3 + 2] = value.getBlue() / 255.0;
        color[colorFrameCount * 4 + 3] = 1;
        colorFrameCount++;
        return this;
    }


    public KeyFramedGlitterBox size(Easing easing, double value) {
        sizeEasings[sizeFrameCount] = KeyFramedGlitter.easingArray.indexOf(easing);
        size[sizeFrameCount] = value;
        sizeFrameCount++;
        return this;
    }

    public KeyFramedGlitterBox alpha(Easing easing, double value) {
        alphaEasings[alphaFrameCount] = KeyFramedGlitter.easingArray.indexOf(easing);
        alpha[alphaFrameCount] = value;
        alphaFrameCount++;
        return this;
    }

}
