//package com.teamwizardry.wizardry.client.particle;
//
//import com.teamwizardry.librarianlib.math.Easing;
//import net.minecraft.util.math.vector.Vector3d;
//
//import java.awt.*;
//
//public class KeyFramedGlitterBox {
//
//    public final int lifetime;
//
//    public int posFrameCount = 0;
//    public double[] pos = new double[3 * 5];
//    public double[] posEasings = new double[]{0, 0, 0, 0, 0};
//
//    public int colorFrameCount = 0;
//    public double[] color = new double[5 * 4];
//    public double[] colorEasings = new double[]{0, 0, 0, 0, 0};
//
//    public int sizeFrameCount = 0;
//    public double[] size = new double[5];
//    public double[] sizeEasings = new double[]{0, 0, 0, 0, 0};
//
//    public int alphaFrameCount = 0;
//    public double[] alpha = new double[5];
//    public double[] alphaEasings = new double[]{0, 0, 0, 0, 0};
//
//    public KeyFramedGlitterBox(int lifetime) {
//        this.lifetime = lifetime;
//    }
//
//    public KeyFramedGlitterBox pos(double x, double y, double z) {
//        return pos(x, y, z, null);
//    }
//
//    public KeyFramedGlitterBox pos(double x, double y, double z, Easing easing) {
//        posEasings[posFrameCount] = easing == null ? 0 : KeyFramedGlitter.easingArray.indexOf(easing);
//        pos[posFrameCount * 3] = x;
//        pos[posFrameCount * 3 + 1] = y;
//        pos[posFrameCount * 3 + 2] = z;
//        posFrameCount++;
//        return this;
//    }
//
//    public KeyFramedGlitterBox pos(Vector3d vec) {
//        return pos(vec, null);
//    }
//
//    public KeyFramedGlitterBox pos(Vector3d vec, Easing easing) {
//        posEasings[posFrameCount] = easing == null ? 0 : KeyFramedGlitter.easingArray.indexOf(easing);
//        pos[posFrameCount * 3] = vec.x;
//        pos[posFrameCount * 3 + 1] = vec.y;
//        pos[posFrameCount * 3 + 2] = vec.z;
//        posFrameCount++;
//        return this;
//    }
//
//    public KeyFramedGlitterBox color(double r, double g, double b) {
//        return color(r, g, b, null);
//    }
//
//    public KeyFramedGlitterBox color(double r, double g, double b, Easing easing) {
//        colorEasings[colorFrameCount] = easing == null ? 0 : KeyFramedGlitter.easingArray.indexOf(easing);
//        color[colorFrameCount * 4] = r;
//        color[colorFrameCount * 4 + 1] = g;
//        color[colorFrameCount * 4 + 2] = b;
//        color[colorFrameCount * 4 + 3] = 1;
//        colorFrameCount++;
//        return this;
//    }
//
//    public KeyFramedGlitterBox color(Color value) {
//        return color(value, null);
//    }
//
//    public KeyFramedGlitterBox color(Color value, Easing easing) {
//        colorEasings[colorFrameCount] = easing == null ? 0 : KeyFramedGlitter.easingArray.indexOf(easing);
//        color[colorFrameCount * 4] = value.getRed() / 255.0;
//        color[colorFrameCount * 4 + 1] = value.getGreen() / 255.0;
//        color[colorFrameCount * 4 + 2] = value.getBlue() / 255.0;
//        color[colorFrameCount * 4 + 3] = 1;
//        colorFrameCount++;
//        return this;
//    }
//
//    public KeyFramedGlitterBox size(double value) {
//        return size(value, null);
//    }
//
//    public KeyFramedGlitterBox size(double value, Easing easing) {
//        sizeEasings[sizeFrameCount] = easing == null ? 0 : KeyFramedGlitter.easingArray.indexOf(easing);
//        size[sizeFrameCount] = value;
//        sizeFrameCount++;
//        return this;
//    }
//
//    public KeyFramedGlitterBox alpha(double value) {
//        return alpha(value, null);
//    }
//
//    public KeyFramedGlitterBox alpha(double value, Easing easing) {
//        alphaEasings[alphaFrameCount] = easing == null ? 0 : KeyFramedGlitter.easingArray.indexOf(easing);
//        alpha[alphaFrameCount] = value;
//        alphaFrameCount++;
//        return this;
//    }
//
//}
