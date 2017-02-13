package com.teamwizardry.wizardry.client.render;

import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.common.tile.TileManaBattery;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad.
 */
public class TileManaBatteryRenderer extends TileEntitySpecialRenderer<TileManaBattery> {

	@Override
	public void renderTileEntityAt(TileManaBattery te, double x, double y, double z, float partialTicks, int destroyStage) {
		World world = te.getWorld();

		PosUtils.ManaBatteryPositions positions = new PosUtils.ManaBatteryPositions(world, te.getPos());
		for (BlockPos pos : positions.fullCircle)
			if (ThreadLocalRandom.current().nextInt(5) == 0)
				LibParticles.MAGIC_DOT(world, new Vec3d(pos).addVector(0.5, 0.5, 0.5), -1);

		for (BlockPos pos : positions.missingSymmetry)
			if (ThreadLocalRandom.current().nextInt(10) == 0)
				LibParticles.MAGIC_DOT(world, new Vec3d(pos).addVector(0.5, 0.5, 0.5), (float) ThreadLocalRandom.current().nextDouble(1, 4));

		for (BlockPos pos : positions.takenPoses)
			if (ThreadLocalRandom.current().nextInt(10) == 0)
				LibParticles.COLORFUL_BATTERY_BEZIER(world, pos, pos);
	}
}
