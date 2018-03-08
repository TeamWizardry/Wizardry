package com.teamwizardry.wizardry.api.util;

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Demoniaque.
 */
public class Utils {

	public static boolean isLyingInCone(Vec3d point, Vec3d coneApex, Vec3d baseCenter, float aperture) {
		float[] x = new float[]{(float) point.x, (float) point.y, (float) point.z};
		float[] t = new float[]{(float) coneApex.x, (float) coneApex.y, (float) coneApex.z};
		float[] b = new float[]{(float) baseCenter.x, (float) baseCenter.y, (float) baseCenter.z};
		return isLyingInCone(x, t, b, aperture);
	}

	/**
	 * @param x        coordinates of point to be tested
	 * @param t        coordinates of apex point of cone
	 * @param b        coordinates of center of basement circle
	 * @param aperture in radians
	 */
	private static boolean isLyingInCone(float[] x, float[] t, float[] b, float aperture) {

		// This is for our convenience
		float halfAperture = aperture / 2.f;

		// Vector pointing to X point from apex
		float[] apexToXVect = dif(t, x);

		// Vector pointing from apex to circle-center point.
		float[] axisVect = dif(t, b);

		// X is lying in cone only if it's lying in
		// infinite version of its cone -- that is,
		// not limited by "round basement".
		// We'll use dotProd() to
		// determine angle between apexToXVect and axis.
		boolean isInInfiniteCone = dotProd(apexToXVect, axisVect)
				/ magn(apexToXVect) / magn(axisVect)
				>
				// We can safely compare cos() of angles
				// between vectors instead of bare angles.
				Math.cos(halfAperture);


		if (!isInInfiniteCone) return false;

		// X is contained in cone only if projection of apexToXVect to axis
		// is shorter than axis.
		// We'll use dotProd() to figure projection length.
		return dotProd(apexToXVect, axisVect)
				/ magn(axisVect)
				<
				magn(axisVect);
	}

	private static float dotProd(float[] a, float[] b) {
		return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
	}

	private static float[] dif(float[] a, float[] b) {
		return (new float[]{
				a[0] - b[0],
				a[1] - b[1],
				a[2] - b[2]
		});
	}

	private static float magn(float[] a) {
		return (float) (Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]));
	}

	/**
	 * Compares versions
	 *
	 * @param ver1
	 * @param ver2
	 * @return 1 if {@code ver1} is > {@code ver2}, -1 if {@code ver1} is < {@code ver2} and 0 otherwise
	 */
	public static int compareVersions(String ver1, String ver2) {
		if (ver1 == null || ver2 == null) return 0;

		String v1 = ver1.replaceAll("[^0-9]+", "");
		String v2 = ver2.replaceAll("[^0-9]+", "");
		int len1 = v1.length();
		int len2 = v2.length();
		// trim any trailing 0 (for comparing cases 6.3 and 6.3.0)
		if (len1 > 0 && len2 > 0) {
			v1 = (v1.charAt(len1 - 1) == '0') ? v1.substring(0, len1 - 1) : v1;
			v2 = (v2.charAt(len2 - 1) == '0') ? v2.substring(0, len2 - 1) : v2;
		}
		int res = v1.compareTo(v2);
		return res < 0 ? -1 : res > 0 ? 1 : res;
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

	public static List<GuiComponent> getVisibleComponents(List<GuiComponent> objects, double scroll) {
		final int ROWS = 3;
		final int COLS = 5;
		final int SPACES = ROWS * COLS;

		if (objects.size() <= ROWS * COLS) return objects;

		int rows = MathHelper.ceil((objects.size() - SPACES) / ((double) COLS));
		double rowsScrolled = scroll * rows;

		int startIndex = ((int) rowsScrolled) * COLS;

		int endIndex = startIndex + SPACES;

		if (startIndex < 0)
			startIndex = 0;

		if (endIndex > objects.size())
			endIndex = objects.size();

		List<GuiComponent> visible = new ArrayList<>();
		visible.addAll(objects.subList(startIndex, endIndex));

		return visible;
	}

	public static int getSlotFor(EntityPlayer player, ItemStack stack) {
		for (int i = 0; i < player.inventory.mainInventory.size(); ++i) {
			if (!player.inventory.mainInventory.get(i).isEmpty() && stackEqualExact(stack, player.inventory.mainInventory.get(i))) {
				return i;
			}
		}

		return -1;
	}

	public static boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
		return stack1.getItem() == stack2.getItem() && (!stack1.getHasSubtypes() || stack1.getMetadata() == stack2.getMetadata()) && ItemStack.areItemStackTagsEqual(stack1, stack2);
	}

}
