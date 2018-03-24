package com.teamwizardry.wizardry.client.render.block;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.tile.TileManaMagnet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class TileManaMagnetRenderer extends TileEntitySpecialRenderer<TileManaMagnet> {

	private static ResourceLocation manaPearlCubeTexture = new ResourceLocation(Wizardry.MODID, "textures/blocks/mana_orb_cube.png");

	@Override
	public void render(TileManaMagnet te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (te.manaOrb != null) {
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
			GlStateManager.translate(0, MathHelper.sin(ClientTickHandler.getTicks()) / 2.0, 0);
			GlStateManager.disableRescaleNormal();
			{
				GlStateManager.enableRescaleNormal();
				GlStateManager.disableCull();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.enableLighting();
				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				RenderHelper.disableStandardItemLighting();

				Color c = Color.WHITE;
				Minecraft.getMinecraft().getTextureManager().bindTexture(manaPearlCubeTexture);

				Tessellator tess = Tessellator.getInstance();
				BufferBuilder buffer = tess.getBuffer();

				double s = 0.1;
				// TOP
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
				buffer.pos(-s, s, -s).tex(0, 0).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
				buffer.pos(-s, s, s).tex(1, 0).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
				buffer.pos(s, s, s).tex(1, 1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
				buffer.pos(s, s, -s).tex(0, 1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
				tess.draw();

				// BOTTOM
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
				buffer.pos(-s, -s, -s).tex(0, 0).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
				buffer.pos(-s, -s, s).tex(1, 0).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
				buffer.pos(s, -s, s).tex(1, 1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
				buffer.pos(s, -s, -s).tex(0, 1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
				tess.draw();

				// TO THE RIGHT
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
				buffer.pos(-s, -s, s).tex(0, 0).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
				buffer.pos(-s, s, s).tex(1, 0).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
				buffer.pos(s, s, s).tex(1, 1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
				buffer.pos(s, -s, s).tex(0, 1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
				tess.draw();

				// TO THE LEFT
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
				buffer.pos(-s, -s, -s).tex(0, 0).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
				buffer.pos(-s, s, -s).tex(1, 0).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
				buffer.pos(s, s, -s).tex(1, 1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
				buffer.pos(s, -s, -s).tex(0, 1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
				tess.draw();

				// FRONT
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
				buffer.pos(s, -s, -s).tex(0, 0).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
				buffer.pos(s, s, -s).tex(1, 0).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
				buffer.pos(s, s, s).tex(1, 1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
				buffer.pos(s, -s, s).tex(0, 1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
				tess.draw();

				// BACK
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
				buffer.pos(-s, -s, -s).tex(0, 0).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
				buffer.pos(-s, s, -s).tex(1, 0).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
				buffer.pos(-s, s, s).tex(1, 1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
				buffer.pos(-s, -s, s).tex(0, 1).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
				tess.draw();

				GlStateManager.disableRescaleNormal();
			}

			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}
}
