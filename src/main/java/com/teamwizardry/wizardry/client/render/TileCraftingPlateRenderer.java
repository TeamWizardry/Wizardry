package com.teamwizardry.wizardry.client.render;

import com.teamwizardry.librarianlib.common.util.math.interpolate.position.InterpBezier3D;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate.ClusterObject;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Saad on 6/11/2016.
 */
public class TileCraftingPlateRenderer extends TileEntitySpecialRenderer<TileCraftingPlate> {

	@Override
	public void renderTileEntityAt(TileCraftingPlate te, double x, double y, double z, float partialTicks, int destroyStage) {

		if (te.isCrafting && (te.output != null)) {
			LibParticles.CRAFTING_ALTAR_HELIX(te.getWorld(), new Vec3d(te.getPos()).addVector(0.5, 0.25, 0.5));
			if (!te.inventory.isEmpty())
				for (ClusterObject cluster : te.inventory) {
					if (((ThreadLocalRandom.current().nextInt(10)) != 0)) continue;
					LibParticles.CRAFTING_ALTAR_CLUSTER_SUCTION(te.getWorld(), new Vec3d(te.getPos()).addVector(0.5, 0.5, 0.5), new InterpBezier3D(cluster.current, new Vec3d(0, 0, 0)));
				}
		}

		if (!te.isCrafting && !te.inventory.isEmpty())
			for (int i = 0; i < ThreadLocalRandom.current().nextInt(1, 10); i++) {
				ClusterObject cluster = te.inventory.get(ThreadLocalRandom.current().nextInt(te.inventory.size()));
				LibParticles.CLUSTER_DRAPE(te.getWorld(), new Vec3d(te.getPos()).addVector(0.5, 0.5, 0.5).add(cluster.current));
			}

		for (ClusterObject cluster : te.inventory) {
			double timeDifference = (te.getWorld().getTotalWorldTime() - cluster.worldTime);
			Vec3d current = cluster.trail.get((float) timeDifference / cluster.destTime);

			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5 + current.xCoord, y + 0.5 + current.yCoord, z + 0.5 + current.zCoord);
			GlStateManager.scale(0.3, 0.3, 0.3);
			GlStateManager.rotate((float) cluster.tick, 0, 1, 0);
			Minecraft.getMinecraft().getRenderItem().renderItem(cluster.stack, TransformType.NONE);
			GlStateManager.popMatrix();
		}

		if (!te.isCrafting && (te.output != null)) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5, y + 1, z + 0.5);
			GlStateManager.scale(0.4, 0.4, 0.4);
			GlStateManager.rotate(te.tick, 0, 1, 0);
			Minecraft.getMinecraft().getRenderItem().renderItem(te.output, TransformType.NONE);
			GlStateManager.popMatrix();
		}
	}

}
