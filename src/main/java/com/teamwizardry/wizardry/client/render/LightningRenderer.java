package com.teamwizardry.wizardry.client.render;

import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by LordSaad.
 */
public class LightningRenderer {

	public static LightningRenderer INSTANCE = new LightningRenderer();
	private static Sprite sprite = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/particles/h_streak.png"));
	public int renderTick = 0;
	public ArrayList<Vec3d> points = new ArrayList<>();

	public LightningRenderer() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	private static VertexBuffer pos(VertexBuffer vb, Vec3d pos) {
		return vb.pos(pos.xCoord, pos.yCoord, pos.zCoord);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void render(RenderWorldLastEvent event) {
		if (points.isEmpty()) return;

		if (--renderTick < 0) {
			points.clear();
			return;
		}

		GlStateManager.pushMatrix();
		EntityPlayer player = Minecraft.getMinecraft().player;

		double interpPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
		double interpPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
		double interpPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();

		GlStateManager.translate(-interpPosX, -interpPosY, -interpPosZ);

		GlStateManager.disableCull();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();

		sprite.bind();

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vb = tessellator.getBuffer();

		for (int i = 1; i < points.size() - 1; i++) {
			Vec3d to = points.get(i);
			Vec3d from = points.get(i - 1);
			double dist = to.distanceTo(from);

			Color color = Color.WHITE;
			Vec3d d = new Vec3d(0, (0.25 * color.getAlpha() / 255f) / 2.0, 0);

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			pos(vb, from.add(d)).tex(0, 0).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();
			pos(vb, from.subtract(d)).tex(0, 1).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();
			pos(vb, to.add(d)).tex(1, 1).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();
			pos(vb, to.subtract(d)).tex(1, 0).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();
			tessellator.draw();

			//sprite.draw(0, (float) from.xCoord, (float) from.yCoord, (float) dist, sprite.getHeight());
		}

		GlStateManager.popMatrix();
	}

}
