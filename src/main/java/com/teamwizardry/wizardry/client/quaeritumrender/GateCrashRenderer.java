package com.teamwizardry.wizardry.client.quaeritumrender;

import com.teamwizardry.librarianlib.features.forgeevents.CustomWorldRenderEvent;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.awt.*;

import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL14.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14.GL_FUNC_REVERSE_SUBTRACT;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = Wizardry.MODID)
public class GateCrashRenderer {

	@SubscribeEvent
	public static void tickDisplay(CustomWorldRenderEvent event) {
		if (Minecraft.getMinecraft().player == null) return;
		if (Minecraft.getMinecraft().world == null) return;

		EntityPlayer player = Minecraft.getMinecraft().player;
		World world = Minecraft.getMinecraft().world;

		GlStateManager.pushMatrix();

		GlStateManager.disableDepth();

		GlStateManager.disableCull();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE);
		//GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.disableTexture2D();
		GlStateManager.enableColorMaterial();

		Vec3d look = player.getLook(0);
		float rY = Utils.signAngle(new Vec3d(0, 0, 1), (new Vec3d(look.x, 0, look.z)).normalize(), new Vec3d(0, 1, 0));
		float rX = Utils.signAngle(new Vec3d(0, 1, 0), look, null);
		GlStateManager.translate(0, 10, 0);
		GlStateManager.rotate(rY, 0f, 1f, 0f);
		GlStateManager.rotate(90 + rX, 1f, 0f, 0f);

		// FLAT WHITE
		GL14.glBlendEquation(GL_FUNC_ADD);
		//drawCircle(0.7f, Color.BLUE, new Color(0, 0, 0, 0));
		//drawCircle(0.7f, Color.BLUE, new Color(0, 0, 0, 0));
		//drawCircle(0.9f, Color.BLUE, new Color(0, 0, 0, 0));
		//drawCircle(1f, Color.BLUE, new Color(0, 0, 0, 0));

		//GL14.glBlendEquation(GL_FUNC_SUBTRACT);
		//drawCircle(0.3f, Color.CYAN, Color.WHITE);

		// OUTER BLUE TO ALPHA
		GL14.glBlendEquation(GL_FUNC_REVERSE_SUBTRACT);

		//drawCircle(0.7f, Color.YELLOW, Color.BLACK);

		drawRing(0.4f, 0.42f, Color.WHITE, Color.BLACK);
		drawRing(0.1f, 0.4f, Color.GREEN, Color.WHITE);
		drawCircle(0.1f, Color.GREEN, Color.GREEN);

		GL14.glBlendEquation(GL_FUNC_ADD);

		// WHTE TO ALPHA
		drawCircle(0.08f, Color.WHITE, new Color(0xFFFFFFFF, true));
		drawRing(0.08f, 0.085f, Color.WHITE, new Color(0x00FFFFFF, true));

		GlStateManager.disableBlend();
		GlStateManager.enableDepth();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
		GlStateManager.disableColorMaterial();

		GlStateManager.enableDepth();
		GlStateManager.popMatrix();
	}

	private static void drawCircle(float radius, Color inner, Color outer) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vb = tessellator.getBuffer();

		vb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
		float px = 0, py = 0;
		vb.pos(0, 0, 0).color(inner.getRed(), inner.getGreen(), inner.getBlue(), inner.getAlpha()).endVertex();

		for (int i = 0; i <= 40; i++) {
			float angle = (float) (i * Math.PI * 2 / 40.0);

			float x1 = MathHelper.cos(angle) * radius;
			float y1 = MathHelper.sin(angle) * radius;

			vb.pos(px, py, 0).color(outer.getRed(), outer.getGreen(), outer.getBlue(), outer.getAlpha()).endVertex();
			vb.pos(x1, y1, 0).color(outer.getRed(), outer.getGreen(), outer.getBlue(), outer.getAlpha()).endVertex();

			px = x1;
			py = y1;
		}
		tessellator.draw();
	}

	private static void drawRing(float innerRadius, float outerRadius, Color inner, Color outer) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vb = tessellator.getBuffer();

		int sections = 40;
		float epsilon = 2 * (float) Math.PI / sections;

		vb.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
		for (int i = 0; i <= sections; i++) {
			float angle = i * epsilon;
			float nextAngle = (i + 1) * epsilon;

			float x1 = MathHelper.cos(angle) * innerRadius;
			float y1 = MathHelper.sin(angle) * innerRadius;

			float x2 = MathHelper.cos(nextAngle) * outerRadius;
			float y2 = MathHelper.sin(nextAngle) * outerRadius;

			vb.pos(x1, y1, 0).color(inner.getRed(), inner.getGreen(), inner.getBlue(), inner.getAlpha()).endVertex();
			vb.pos(x2, y2, 0).color(outer.getRed(), outer.getGreen(), outer.getBlue(), outer.getAlpha()).endVertex();
		}
		tessellator.draw();
	}
}
