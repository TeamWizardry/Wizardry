package com.teamwizardry.wizardry.api.util;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by LordSaad.
 */
public class Utils {

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

	public static boolean hasOreDictPrefix(ItemStack stack, String dict) {
		int[] ids = OreDictionary.getOreIDs(stack);
		for (int id : ids) {
			if (OreDictionary.getOreName(id).length() >= dict.length()) {
				if (OreDictionary.getOreName(id).substring(0, dict.length()).compareTo(dict.substring(0, dict.length())) == 0) {
					return true;
				}
			}
		}
		return false;
	}
}
