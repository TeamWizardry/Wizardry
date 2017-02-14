package com.teamwizardry.wizardry.api.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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

	public static Color changeColorAlpha(Color color, int newAlpha) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), newAlpha);
	}

	public static Color shiftColorHueRandomly(Color color, double shiftAmount) {
		return new Color(
				(int) Math.max(0, Math.min(color.getRed() + ThreadLocalRandom.current().nextDouble(-shiftAmount, shiftAmount), 255)),
				(int) Math.max(0, Math.min(color.getGreen() + ThreadLocalRandom.current().nextDouble(-shiftAmount, shiftAmount), 255)),
				(int) Math.max(0, Math.min(color.getBlue() + ThreadLocalRandom.current().nextDouble(-shiftAmount, shiftAmount), 255)));
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

	public static ItemStack getItemInHand(EntityPlayer player, Item item) {
		ItemStack stack = player.getHeldItemMainhand();
		if (stack == null)
			stack = player.getHeldItemOffhand();

		if (stack == null)
			return null;
		if (stack.getItem() != item)
			return null;

		return stack;
	}

	public static Vec3d getProjectileSlope(Vec3d origin, Vec3d target) {
		double payloadFrictionY = 0.98D;
		double payloadFrictionX = 0.98D;
		double payloadGravity = 0.04D;

		double deltaX = origin.xCoord - target.xCoord;
		double deltaZ = origin.zCoord - target.zCoord;
		float calculatedRotationAngle;
		if (deltaX >= 0 && deltaZ < 0) {
			calculatedRotationAngle = (float) (Math.atan(Math.abs(deltaX / deltaZ)) / Math.PI * 180D);
		} else if (deltaX >= 0 && deltaZ >= 0) {
			calculatedRotationAngle = (float) (Math.atan(Math.abs(deltaZ / deltaX)) / Math.PI * 180D) + 90;
		} else if (deltaX < 0 && deltaZ >= 0) {
			calculatedRotationAngle = (float) (Math.atan(Math.abs(deltaX / deltaZ)) / Math.PI * 180D) + 180;
		} else calculatedRotationAngle = (float) (Math.atan(Math.abs(deltaZ / deltaX)) / Math.PI * 180D) + 270;

		double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
		double deltaY = origin.yCoord - target.yCoord;
		float calculatedHeightAngle = calculateBestHeightAngle(distance, deltaY, 1f, payloadGravity, payloadFrictionX, payloadFrictionY);

		double[] set = getVelocityVector(calculatedRotationAngle, calculatedHeightAngle, 1f);
		return new Vec3d(set[0], set[1], set[2]);
	}

	private static float calculateBestHeightAngle(double distance, double deltaY, float force, double payloadGravity, double payloadFrictionX, double payloadFrictionY) {
		double bestAngle = 0;
		double bestDistance = Float.MAX_VALUE;
		if (payloadGravity == 0D) {
			return 90F - (float) (Math.atan(deltaY / distance) * 180F / Math.PI);
		}
		for (double i = Math.PI * 0.25D; i < Math.PI * 0.50D; i += 0.001D) {
			double motionX = Math.cos(i) * force;
			double motionY = Math.sin(i) * force;
			double posX = 0;
			double posY = 0;
			while (posY > deltaY || motionY > 0) {
				posX += motionX;
				posY += motionY;
				motionY -= payloadGravity;
				motionX *= payloadFrictionX;
				motionY *= payloadFrictionY;
			}
			double distanceToTarget = Math.abs(distance - posX);
			if (distanceToTarget < bestDistance) {
				bestDistance = distanceToTarget;
				bestAngle = i;
			}
		}
		return 90F - (float) (bestAngle * 180D / Math.PI);
	}

	public static double[] getVelocityVector(float angleX, float angleZ, float force) {
		double[] velocities = new double[3];
		velocities[0] = Math.sin((double) angleZ / 180 * Math.PI);
		velocities[1] = Math.cos((double) angleX / 180 * Math.PI);
		velocities[2] = Math.cos((double) angleZ / 180 * Math.PI) * -1;

		velocities[0] *= Math.sin((double) angleX / 180 * Math.PI);
		velocities[2] *= Math.sin((double) angleX / 180 * Math.PI);
		double vectorTotal = velocities[0] * velocities[0] + velocities[1] * velocities[1] + velocities[2] * velocities[2];
		vectorTotal = force / vectorTotal;
		for (int i = 0; i < 3; i++) {
			velocities[i] *= vectorTotal;
		}
		return velocities;
	}
}
