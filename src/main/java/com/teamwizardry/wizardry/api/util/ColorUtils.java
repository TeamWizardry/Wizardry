package com.teamwizardry.wizardry.api.util;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;

/**
 * Created by Demoniaque.
 */
public class ColorUtils {

	public static Color mixColors(List<Color> colors) {
		int red = 0, green = 0, blue = 0;

		for (Color color : colors) {

			double colorCount = 0;
			if (color.getRed() > 0) colorCount++;
			if (color.getGreen() > 0) colorCount++;
			if (color.getBlue() > 0) colorCount++;
			if (colorCount <= 0) continue;

			red += color.getRed() * color.getAlpha() / 255F / colorCount;
			green += color.getGreen() * color.getAlpha() / 255F / colorCount;
			blue += color.getBlue() * color.getAlpha() / 255F / colorCount;
		}

		if (!colors.isEmpty()) {
			red = Math.min(red / colors.size(), 255);
			green = Math.min(green / colors.size(), 255);
			blue = Math.min(blue / colors.size(), 255);
		}

		return new Color(red, green, blue);
	}

	public static Color mixColors(@Nonnull Color color1, @Nonnull Color color2) {

		float r = ((color1.getRed() / 255f) + (color2.getRed() / 255f)) / 2f;
		float g = ((color1.getGreen() / 255f) + (color2.getGreen() / 255f)) / 2f;
		float b = ((color1.getBlue() / 255f) + (color2.getBlue() / 255f)) / 2f;
		float a = ((color1.getAlpha() / 255f) + (color2.getAlpha() / 255f)) / 2f;

		if (Math.round(r * 255f) == color2.getRed()) r = color2.getRed() / 255f;
		if (Math.round(g * 255f) == color2.getGreen()) g = color2.getGreen() / 255f;
		if (Math.round(b * 255f) == color2.getBlue()) b = color2.getBlue() / 255f;
		if (Math.round(a * 255f) == color2.getAlpha()) a = color2.getAlpha() / 255f;

		return new Color(r, g, b, a);
	}

	public static Color changeColorAlpha(@Nonnull Color color, int newAlpha) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), newAlpha);
	}

	public static Color shiftColorHueRandomly(@Nonnull Color color, double shiftAmount) {
		return new Color(
				(int) Math.max(0, Math.min(color.getRed() + RandUtil.nextDouble(-shiftAmount, shiftAmount), 255)),
				(int) Math.max(0, Math.min(color.getGreen() + RandUtil.nextDouble(-shiftAmount, shiftAmount), 255)),
				(int) Math.max(0, Math.min(color.getBlue() + RandUtil.nextDouble(-shiftAmount, shiftAmount), 255)));
	}

	public static float[] getHSVFromColor(Color color) {
		return Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
	}

	public static Color getColorFromHSV(float[] hsv) {
		return new Color(Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]));
	}

}
