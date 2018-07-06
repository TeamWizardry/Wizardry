package com.teamwizardry.wizardry.api.util;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

public class RenderUtils {

	public static void renderBlockOutline(AxisAlignedBB aabb) {
		Tessellator tessellator = Tessellator.getInstance();

		double ix = aabb.minX;
		double iy = aabb.minY;
		double iz = aabb.minZ;
		double ax = aabb.maxX;
		double ay = aabb.maxY;
		double az = aabb.maxZ;

		tessellator.getBuffer().begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
		tessellator.getBuffer().pos(ix, iy, iz).endVertex();
		tessellator.getBuffer().pos(ix, ay, iz).endVertex();

		tessellator.getBuffer().pos(ix, ay, iz).endVertex();
		tessellator.getBuffer().pos(ax, ay, iz).endVertex();

		tessellator.getBuffer().pos(ax, ay, iz).endVertex();
		tessellator.getBuffer().pos(ax, iy, iz).endVertex();

		tessellator.getBuffer().pos(ax, iy, iz).endVertex();
		tessellator.getBuffer().pos(ix, iy, iz).endVertex();

		tessellator.getBuffer().pos(ix, iy, az).endVertex();
		tessellator.getBuffer().pos(ix, ay, az).endVertex();

		tessellator.getBuffer().pos(ix, iy, az).endVertex();
		tessellator.getBuffer().pos(ax, iy, az).endVertex();

		tessellator.getBuffer().pos(ax, iy, az).endVertex();
		tessellator.getBuffer().pos(ax, ay, az).endVertex();

		tessellator.getBuffer().pos(ix, ay, az).endVertex();
		tessellator.getBuffer().pos(ax, ay, az).endVertex();

		tessellator.getBuffer().pos(ix, iy, iz).endVertex();
		tessellator.getBuffer().pos(ix, iy, az).endVertex();

		tessellator.getBuffer().pos(ix, ay, iz).endVertex();
		tessellator.getBuffer().pos(ix, ay, az).endVertex();

		tessellator.getBuffer().pos(ax, iy, iz).endVertex();
		tessellator.getBuffer().pos(ax, iy, az).endVertex();

		tessellator.getBuffer().pos(ax, ay, iz).endVertex();
		tessellator.getBuffer().pos(ax, ay, az).endVertex();

		tessellator.draw();
	}
}
