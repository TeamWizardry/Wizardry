package com.teamwizardry.wizardry.api.util;

import java.awt.*;

/**
 * Created by LordSaad.
 */
public class Utils {

	public static Color mixColors(Color color1, Color color2) {
		double inverse_percent = 1.0 - 0.9;
		double redPart = color1.getRed() * 0.9 + color2.getRed() * inverse_percent;
		double greenPart = color1.getGreen() * 0.9 + color2.getGreen() * inverse_percent;
		double bluePart = color1.getBlue() * 0.9 + color2.getBlue() * inverse_percent;
		double alphaPart = color1.getAlpha() * 0.9 + color2.getAlpha() * inverse_percent;
		return new Color((int) redPart, (int) greenPart, (int) bluePart, (int) alphaPart);
	}
}
