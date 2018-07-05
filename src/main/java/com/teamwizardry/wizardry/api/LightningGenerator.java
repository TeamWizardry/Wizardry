package com.teamwizardry.wizardry.api;

import java.util.ArrayList;
import java.util.Collections;

import com.teamwizardry.librarianlib.features.math.interpolate.InterpFunction;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpLine;
import com.teamwizardry.wizardry.api.util.RandUtilSeed;
import com.teamwizardry.wizardry.api.util.TreeNode;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;

/**
 * Created by Demoniaque.
 */
public class LightningGenerator
{
	public static final int POINTS_PER_DIST = 6;
	public static final float ANGLE_OFFSET = 22.5f;
	public static final int MAX_BRANCHES = 2;

	public static TreeNode<Vec3d> generate(RandUtilSeed rand, Vec3d from, Vec3d to, double offshootRange)
	{
		TreeNode<Vec3d> root = new TreeNode<>(from);
		
		generateOffshoot(rand, root, to, offshootRange, MAX_BRANCHES);
		
		return root;
	}
	
	private static TreeNode<Vec3d> generateOffshoot(RandUtilSeed rand, TreeNode<Vec3d> from, Vec3d to, double offshootRange, int numBranchesLeft)
	{
		if (numBranchesLeft <= 0)
			return from;
		
		TreeNode<Vec3d> bolt = from;

		InterpFunction<Vec3d> interp = new InterpLine(from.getData(), to);
		ArrayList<Float> points = new ArrayList<>();

		double dist = to.subtract(from.getData()).lengthVector();
		if (dist * POINTS_PER_DIST < 1)
			return from;
		
		points.add(1f);
		for (int i = 0; i < dist * POINTS_PER_DIST; i++)
			points.add(rand.nextFloat());

		Collections.sort(points);

		for (float point : points)
		{
			double xOff = rand.nextDouble(offshootRange/2, offshootRange) * (rand.nextBoolean() ? 1 : -1);
			double yOff = rand.nextDouble(offshootRange/2, offshootRange) * (rand.nextBoolean() ? 1 : -1);
			double zOff = rand.nextDouble(offshootRange/2, offshootRange) * (rand.nextBoolean() ? 1 : -1);
			Vec3d newPoint = interp.get(point).addVector(xOff, yOff, zOff);
			bolt = bolt.addChild(new TreeNode<>(newPoint));
			while (rand.nextInt(10) == 0)
			{
				double scale = rand.nextDouble(offshootRange/2, offshootRange);
				float pitch = rand.nextFloat(ANGLE_OFFSET, 2*ANGLE_OFFSET) * (rand.nextBoolean() ? 1 : -1);
				float yaw = rand.nextFloat(ANGLE_OFFSET, 2*ANGLE_OFFSET) * (rand.nextBoolean() ? 1 : -1);
				Vec3d newTo = bolt.getData().subtract(bolt.getParent().getData()).normalize().rotatePitch(pitch).rotateYaw(yaw).scale(scale).add(bolt.getParent().getData());
				LightningGenerator.generateOffshoot(rand, bolt.getParent(), newTo, offshootRange, numBranchesLeft-1);
			}
		}
		
		return from;
	}
}
