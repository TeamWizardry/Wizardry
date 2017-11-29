package com.teamwizardry.wizardry.api.render;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public class CPItemRenderer {

	public double x, y, z;
	public double prevX, prevY, prevZ;
	public boolean reset = true;

	public CPItemRenderer(@NotNull ItemStack stack, Vec3d origin) {
		x = prevX = origin.x;
		y = prevY = origin.y;
		z = prevZ = origin.z;
	}
}
