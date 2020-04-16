package com.teamwizardry.wizardry.api.utils;

import java.awt.*;

public class ColorUtils {

	public static Color generateRandomColor() {
		return new Color(RandUtil.nextFloat(), RandUtil.nextFloat(), RandUtil.nextFloat());
	}
}
