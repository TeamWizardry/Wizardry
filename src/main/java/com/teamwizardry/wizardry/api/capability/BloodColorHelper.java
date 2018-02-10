package com.teamwizardry.wizardry.api.capability;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

/**
 * @author WireSegal
 * Created at 3:18 PM on 8/2/16.
 */
@SideOnly(Side.CLIENT)
public final class BloodColorHelper {

	public static int pulseColor(int rgb, int variance) {
		int add = (int) (MathHelper.sin((float) ClientTickHandler.getTicksInGame() * 0.2f) * variance);
		int r = (rgb & (0xFF << 16)) >> 16;
		int b = (rgb & (0xFF << 8)) >> 8;
		int g = (rgb & (0xFF));
		return (Math.max(Math.min(r + add, 255), 0) << 16) |
				(Math.max(Math.min(b + add, 255), 0) << 8) |
				(Math.max(Math.min(g + add, 255), 0));
	}

	public static int pulseColor(int rgb) {
		return pulseColor(rgb, 10);
	}

	public static float[] decomposeColor(int rgb) {
		float[] ret = new float[3];
		ret[0] = ((rgb & (0xFF << 16)) >> 16) / 255f;
		ret[1] = ((rgb & (0xFF << 8)) >> 8) / 255f;
		ret[2] = (rgb & (0xFF)) / 255f;
		return ret;
	}

	public static Color makeColor(int rgb) {
		float[] color = decomposeColor(rgb);
		return new Color(color[0], color[1], color[2]);
	}
}
