package com.teamwizardry.wizardry.api.util;

import com.google.common.collect.Iterables;
import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

@SideOnly(Side.CLIENT)
public class LightEffectUtil {

	public static void renderBall(Vec3d cetner, Color color, double radius, double resolution) {
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vb = tessellator.getBuffer();

		GlStateManager.pushMatrix();
		EntityPlayer player = Minecraft.getMinecraft().player;

		double interpPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * ClientTickHandler.getPartialTicks();
		double interpPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * ClientTickHandler.getPartialTicks();
		double interpPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * ClientTickHandler.getPartialTicks();

		GlStateManager.translate(-interpPosX, -interpPosY, -interpPosZ);

		GlStateManager.depthMask(false);

		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.disableCull();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE);
		GlStateManager.color(1, 1, 1);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
		Minecraft.getMinecraft().entityRenderer.disableLightmap();

		vb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
		float px = (float) cetner.x, py = (float) cetner.y;
		for (int i = 0; i <= resolution; i++) {
			float angle = (float) (i * Math.PI * 2 / resolution);

			float x1 = (float) (cetner.x + MathHelper.cos(angle) * radius);
			float y1 = (float) (cetner.y + MathHelper.sin(angle) * radius);

			pos(vb, cetner).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
			pos(vb, new Vec3d(px, py, cetner.z)).color(0, 0, 0, 0).endVertex();
			pos(vb, new Vec3d(x1, y1, cetner.z)).color(0, 0, 0, 0).endVertex();

			px = x1;
			py = y1;
		}
		tessellator.draw();

		GlStateManager.depthMask(true);
		GlStateManager.popMatrix();
	}

	public static void renderBilinearGradient(List<Vec3d> points, Color color, double thickness, Vec3d normal) {
		List<Vec3d> derivative = derivative(points);

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vb = tessellator.getBuffer();

		GlStateManager.pushMatrix();
		EntityPlayer player = Minecraft.getMinecraft().player;

		double interpPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * ClientTickHandler.getPartialTicks();
		double interpPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * ClientTickHandler.getPartialTicks();
		double interpPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * ClientTickHandler.getPartialTicks();

		GlStateManager.translate(-interpPosX, -interpPosY, -interpPosZ);

		GlStateManager.depthMask(false);

		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.disableCull();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE);
		GlStateManager.color(1, 1, 1);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
		Minecraft.getMinecraft().entityRenderer.disableLightmap();

		Vec3d playerEyes = Minecraft.getMinecraft().player.getPositionEyes(ClientTickHandler.getPartialTicks());
		Vec3d normal1 = (points.get(points.size() - 1).subtract(points.get(0))).crossProduct(playerEyes.subtract(points.get(0))).normalize();
		if (normal1.y < 0) normal1 = normal.scale(-1);

		int a = color.getAlpha();

		for (int i = 1; i < points.size(); i++) {
			if (i - 1 < 0) continue;
			Vec3d to = points.get(i);
			Vec3d from = points.get(i - 1);
			if (derivative.size() <= i) continue;
			Vec3d der = derivative.get(i);

			Vec3d norm = new Vec3d(der.y, -der.x, der.z).normalize().scale(thickness);

			Vec3d midpoint = new Vec3d((from.x + to.x) / 2, (from.y + to.y) / 2, (from.z + to.z) / 2);

			vb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
			pos(vb, from).color(color.getRed(), color.getGreen(), color.getBlue(), a).endVertex();
			pos(vb, midpoint.subtract(norm)).color(0, 0, 0, 0).endVertex();
			pos(vb, to).color(color.getRed(), color.getGreen(), color.getBlue(), a).endVertex();
			tessellator.draw();

			vb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
			pos(vb, from).color(color.getRed(), color.getGreen(), color.getBlue(), a).endVertex();
			pos(vb, midpoint.add(norm)).color(0, 0, 0, 0).endVertex();
			pos(vb, to).color(color.getRed(), color.getGreen(), color.getBlue(), a).endVertex();
			tessellator.draw();

			if (points.size() > i + 1 && derivative.size() > i + 1) {
				Vec3d nextPoint = points.get(i + 1);
				Vec3d nextDer = derivative.get(i + 1);
				Vec3d nextDerNorm = new Vec3d(nextDer.y, -nextDer.x, nextDer.z).normalize().scale(thickness);
				Vec3d nextMidPoint = new Vec3d((to.x + nextPoint.x) / 2, (to.y + nextPoint.y) / 2, (to.z + nextPoint.z) / 2);

				vb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
				pos(vb, midpoint.subtract(norm)).color(0, 0, 0, 0).endVertex();
				pos(vb, nextMidPoint.subtract(nextDerNorm)).color(0, 0, 0, 0).endVertex();
				pos(vb, to).color(color.getRed(), color.getGreen(), color.getBlue(), a).endVertex();
				tessellator.draw();

				vb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
				pos(vb, midpoint.add(norm)).color(0, 0, 0, 0).endVertex();
				pos(vb, nextMidPoint.add(nextDerNorm)).color(0, 0, 0, 0).endVertex();
				pos(vb, to).color(color.getRed(), color.getGreen(), color.getBlue(), a).endVertex();
				tessellator.draw();
			} else {
				vb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
				pos(vb, midpoint.subtract(norm)).color(0, 0, 0, 0).endVertex();
				pos(vb, to.subtract(norm)).color(0, 0, 0, 0).endVertex();
				pos(vb, to).color(color.getRed(), color.getGreen(), color.getBlue(), a).endVertex();
				tessellator.draw();

				vb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
				pos(vb, midpoint.add(norm)).color(0, 0, 0, 0).endVertex();
				pos(vb, to.add(norm)).color(0, 0, 0, 0).endVertex();
				pos(vb, to).color(color.getRed(), color.getGreen(), color.getBlue(), a).endVertex();
				tessellator.draw();
			}
		}

		GlStateManager.depthMask(true);

		GlStateManager.popMatrix();
	}

	private static VertexBuffer pos(VertexBuffer vb, Vec3d pos) {
		return vb.pos(pos.x, pos.y, pos.z);
	}

	private static List<Vec3d> derivative(List<Vec3d> controlPoints) {
		if (controlPoints.isEmpty()) {
			throw new IllegalArgumentException("controlPoints must not be empty");
		}
		int size = controlPoints.size() - 1;
		List<Vec3d> derivative = new ArrayList<>(size);
		Vec3d prev = controlPoints.get(0);
		for (Vec3d vec : Iterables.skip(controlPoints, 1)) {
			derivative.add(prev.subtract(vec).scale(size));
			prev = vec;
		}
		return derivative;
	}
}
