package com.teamwizardry.wizardry.api.util;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad.
 */
public class ColorUtils {

	public static Color mixColors(Color color1, Color color2) {
		double inverse_percent = 1.0 - 0.9;
		double redPart = color1.getRed() * 0.9 + color2.getRed() * inverse_percent;
		double greenPart = color1.getGreen() * 0.9 + color2.getGreen() * inverse_percent;
		double bluePart = color1.getBlue() * 0.9 + color2.getBlue() * inverse_percent;
		double alphaPart = color1.getAlpha() * 0.9 + color2.getAlpha() * inverse_percent;
		return new Color((int) redPart, (int) greenPart, (int) bluePart, (int) alphaPart);
	}

	public static Color changeColorAlpha(Color color, int newAlpha) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), newAlpha);
	}

	public static Color shiftColorHueRandomly(Color color, double shiftAmount) {
		return new Color(
				(int) Math.max(0, Math.min(color.getRed() + ThreadLocalRandom.current().nextDouble(-shiftAmount, shiftAmount), 255)),
				(int) Math.max(0, Math.min(color.getGreen() + ThreadLocalRandom.current().nextDouble(-shiftAmount, shiftAmount), 255)),
				(int) Math.max(0, Math.min(color.getBlue() + ThreadLocalRandom.current().nextDouble(-shiftAmount, shiftAmount), 255)));
	}

	public static float[] getHSVFromColor(Color color) {
		return Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
	}

	public static Color getColorFromHSV(float[] hsv) {
		return new Color(Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]));
	}
}
