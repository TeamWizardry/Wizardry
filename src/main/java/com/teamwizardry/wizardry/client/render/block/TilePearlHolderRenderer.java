package com.teamwizardry.wizardry.client.render.block;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.tile.TilePearlHolder;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

/**
 * Created by Saad on 5/7/2016.
 */
public class TilePearlHolderRenderer extends TileEntitySpecialRenderer<TilePearlHolder> {

	private static ResourceLocation pearlCubeTexture = new ResourceLocation(Wizardry.MODID, "textures/blocks/pearl_cube.png");
	private static ResourceLocation manaPearlCubeTexture = new ResourceLocation(Wizardry.MODID, "textures/blocks/mana_pearl_cube.png");

	@Override
	public void render(TilePearlHolder te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (te.pearl != null && (te.pearl.getItem() == ModItems.MANA_ORB || te.pearl.getItem() == ModItems.PEARL_NACRE)) {

			boolean isPearl = te.pearl.getItem() == ModItems.PEARL_NACRE;

			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			if (Minecraft.isAmbientOcclusionEnabled())
				GlStateManager.shadeModel(GL11.GL_SMOOTH);
			else GlStateManager.shadeModel(GL11.GL_FLAT);

			GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
			GlStateManager.disableRescaleNormal();

			float sin = (float) Math.sin((te.getWorld().getTotalWorldTime() + partialTicks + te.getPos().hashCode()) / 10.0);
			boolean magnetFound = false;

			if (isPearl) {
				Vec3d direction = Vec3d.ZERO;
				for (int i = -6; i < 6; i++)
					for (int j = -6; j < 6; j++)
						for (int k = -6; k < 6; k++) {
							BlockPos pos = new BlockPos(te.getPos().getX() + i, te.getPos().getY() + j, te.getPos().getZ() + k);
							if (te.getWorld().getBlockState(pos).getBlock() != ModBlocks.MANA_MAGNET) continue;
							direction = new Vec3d(te.getPos()).subtract(new Vec3d(pos)).normalize();
							magnetFound = true;
							break;
						}

				if (magnetFound) {
					GlStateManager.translate(sin * direction.x / 5.0, sin * direction.y / 5.0, sin * direction.z / 5.0);
					GlStateManager.translate(-direction.x / 3.0, -direction.y / 3.0, -direction.z / 3.0);
				}
			} else {
				Vec3d directionBattery = Vec3d.ZERO;
				boolean batteryFound = false;
				for (int i = -6; i < 6; i++)
					for (int j = -6; j < 6; j++)
						for (int k = -6; k < 6; k++) {
							BlockPos pos = new BlockPos(te.getPos().getX() + i, te.getPos().getY() + j, te.getPos().getZ() + k);
							if (te.getWorld().getBlockState(pos).getBlock() != ModBlocks.MANA_BATTERY) continue;
							directionBattery = new Vec3d(te.getPos()).subtract(new Vec3d(pos)).normalize();
							batteryFound = true;
							break;
						}
				if (batteryFound) {
					GlStateManager.translate(sin * directionBattery.x / 5.0, sin * directionBattery.y / 5.0, sin * directionBattery.z / 5.0);
					GlStateManager.translate(-directionBattery.x / 3.0, -directionBattery.y / 3.0, -directionBattery.z / 3.0);
				}
			}

			if (!isPearl || !magnetFound) GlStateManager.translate(0, sin / 10.0, 0);

			GlStateManager.rotate((te.getWorld().getTotalWorldTime() + partialTicks) * 4.0f, 0, 1, 0);

			GlStateManager.translate(0, 0.6, 0);
			GlStateManager.rotate(45f, 1, 0, 1);

			{
				GlStateManager.enableRescaleNormal();
				GlStateManager.disableCull();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.enableLighting();
				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				RenderHelper.disableStandardItemLighting();

				Color c = Color.WHITE;
				if (te.pearl.getItem() == ModItems.PEARL_NACRE) {
					c = new Color(Minecraft.getMinecraft().getItemColors().getColorFromItemstack(te.pearl, 0));
					Minecraft.getMinecraft().getTextureManager().bindTexture(pearlCubeTexture);
				} else Minecraft.getMinecraft().getTextureManager().bindTexture(manaPearlCubeTexture);

				Tessellator tess = Tessellator.getInstance();
				BufferBuilder buffer = tess.getBuffer();

				double s = isPearl ? 0.1 : 0.15;
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
