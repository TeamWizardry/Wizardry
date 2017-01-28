package com.teamwizardry.wizardry.api.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

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

	/**
	 * Credits to Masa on discord for providing the base of the code. I heavily editted it.
	 * This raytracer will precisely trace entities and blocks (including misses) without snapping to the grid.
	 *
	 * @param world  The world obj.
	 * @param slope  The angle vector to trace along.
	 * @param origin The origin of the trace.
	 * @param range  The maximum range to trace by.
	 * @param skip   An optional entity you can add to skip the trace with.
	 * @return The RaytraceResult.
	 */
	public static RayTraceResult raytrace(World world, Vec3d slope, Vec3d origin, double range, @Nullable Entity skip) {
		Vec3d lookVec = origin.add(slope.scale(range));

		RayTraceResult result = world.rayTraceBlocks(origin, lookVec, false, false, true);

		Entity targetEntity = null;
		RayTraceResult entityTrace = null;
		AxisAlignedBB bb = new AxisAlignedBB(lookVec.xCoord, lookVec.yCoord, lookVec.zCoord, lookVec.xCoord, lookVec.yCoord, lookVec.zCoord);
		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(skip, bb.expand(range, range, range));
		double closest = 0.0D;

		for (Entity entity : list) {
			bb = entity.getEntityBoundingBox();
			RayTraceResult traceTmp = bb.calculateIntercept(lookVec, origin);

			if (traceTmp != null) {
				double tmp = origin.distanceTo(traceTmp.hitVec);

				if (tmp < closest || closest == 0.0D) {
					targetEntity = entity;
					entityTrace = traceTmp;
					closest = tmp;
				}
			}
		}

		if (targetEntity != null) result = new RayTraceResult(targetEntity, entityTrace.hitVec);

		return result;
	}
}
