package com.teamwizardry.wizardry.api.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

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

	public static void blink(EntityLivingBase entity, double dist) {
		if (entity == null) return;
		Vec3d look = entity.getLookVec();

		double x = entity.posX += look.xCoord * dist;
		double y = entity.posY += Math.max(0, look.yCoord * dist);
		double z = entity.posZ += look.zCoord * dist;

		if (entity instanceof EntityPlayerMP) {
			EntityPlayerMP mp = (EntityPlayerMP) entity;
			mp.connection.setPlayerLocation(x, y, z, entity.rotationYaw, entity.rotationPitch);
		} else entity.setPosition(x, y, z);
	}

	public static RayTraceResult getTargetBlock(World world, Entity entity, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, double range) {
		float var4 = 1.0F;
		float var5 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * var4;
		float var6 = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * var4;
		double var7 = entity.prevPosX + (entity.posX - entity.prevPosX) * var4;
		double var9 = entity.prevPosY + (entity.posY - entity.prevPosY) * var4 + entity.getEyeHeight();
		double var11 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * var4;
		Vec3d var13 = new Vec3d(var7, var9, var11);
		float var14 = MathHelper.cos(-var6 * 0.017453292F - 3.1415927F);
		float var15 = MathHelper.sin(-var6 * 0.017453292F - 3.1415927F);
		float var16 = -MathHelper.cos(-var5 * 0.017453292F);
		float var17 = MathHelper.sin(-var5 * 0.017453292F);
		float var18 = var15 * var16;
		float var20 = var14 * var16;
		Vec3d var23 = var13.addVector(var18 * range, var17 * range, var20 * range);
		return world.rayTraceBlocks(var13, var23, stopOnLiquid, !ignoreBlockWithoutBoundingBox, false);
	}
}
