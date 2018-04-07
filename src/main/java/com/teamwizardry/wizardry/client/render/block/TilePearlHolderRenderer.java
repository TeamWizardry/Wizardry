package com.teamwizardry.wizardry.client.render.block;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.common.tile.TilePearlHolder;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * Created by Demoniaque on 5/7/2016.
 */
public class TilePearlHolderRenderer extends TileEntitySpecialRenderer<TilePearlHolder> {

	private static ResourceLocation pearlTexture = new ResourceLocation(Wizardry.MODID, "textures/blocks/pearl_cube.png");
	private static ResourceLocation manaOrb = new ResourceLocation(Wizardry.MODID, "textures/blocks/mana_orb_cube.png");
	private static ResourceLocation glassOrb = new ResourceLocation(Wizardry.MODID, "textures/blocks/glass_orb_cube.png");

	@Override
	public void render(TilePearlHolder te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (te.containsSomething()) {

			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
			GlStateManager.disableRescaleNormal();

			float sin = (float) Math.sin((te.getWorld().getTotalWorldTime() + partialTicks + te.getPos().hashCode()) / 10.0);

			boolean gravitating = false;
			// Hover towards block
			if (te.containsSomething()) {
				BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(te.getPos());
				for (int i = -6; i < 6; i++)
					for (int j = -6; j < 6; j++)
						for (int k = -6; k < 6; k++) {
							pos.setPos(te.getPos().getX() + i, te.getPos().getY() + j, te.getPos().getZ() + k);
							Block block = te.getWorld().getBlockState(pos).getBlock();

							if (te.containsNacrePearl() && block == ModBlocks.MANA_MAGNET) {
								gravitating = true;
								Vec3d direction = new Vec3d(te.getPos()).subtract(new Vec3d(pos)).normalize();
								GlStateManager.translate(sin * direction.x / 5.0, sin * direction.y / 5.0, sin * direction.z / 5.0);
								break;
							}
							if (te.containsAnyOrb() && block == ModBlocks.MANA_BATTERY) {
								gravitating = true;
								Vec3d direction = new Vec3d(te.getPos()).subtract(new Vec3d(pos)).normalize();
								GlStateManager.translate(sin * direction.x / 5.0, sin * direction.y / 5.0, sin * direction.z / 5.0);
								break;
							}
						}
			}


			if (!gravitating) GlStateManager.translate(0, sin / 10.0, 0);

			GlStateManager.rotate((te.getWorld().getTotalWorldTime() + partialTicks) * 4.0f, 0, 1, 0);

			GlStateManager.translate(0, 0.6, 0);
			GlStateManager.rotate(45f, 1, 0, 1);

			{
				GlStateManager.disableCull();
				GlStateManager.enableLighting();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				RenderHelper.disableStandardItemLighting();

				if (te.containsNacrePearl()) {
					Minecraft.getMinecraft().getTextureManager().bindTexture(pearlTexture);
					renderCube(0.1, new Color(Minecraft.getMinecraft().getItemColors().colorMultiplier(te.getItemStack(), 0)));
				} else if (te.containsAnyOrb()) {
					CapManager manager = new CapManager(te.getWizardryCap());
					Color c = new Color(1f, 1f, 1f, (float) (manager.getMana() / manager.getMaxMana()));
					Minecraft.getMinecraft().getTextureManager().bindTexture(manaOrb);
					renderCube(0.13, c);
					Minecraft.getMinecraft().getTextureManager().bindTexture(glassOrb);
					renderCube(0.135, new Color(1, 1, 1, 0.8f));
				}
				GlStateManager.disableRescaleNormal();
			}

			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}

	// Texture Unit for cube textures
	private static final double TX = 0.25;

	private static void side(Tessellator tess, double scale, Color color, EnumFacing facing, double u, double v) {
		int normalX = facing.getFrontOffsetX();
		int normalY = facing.getFrontOffsetY();
		int normalZ = facing.getFrontOffsetZ();

		BufferBuilder bf = tess.getBuffer();

		bf.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX_COLOR);

		for (int vertex = 0; vertex <= 0b11; vertex++) {
			int vertexU = (vertex & 0b1);
			int vertexV = (vertex & 0b10) >> 1;

			int crossU = 1 - 2 * vertexU;
			int crossV = 1 - 2 * vertexV;

			int x = normalX == 0 ? crossU : normalX;
			int z = normalZ == 0 ? crossV : normalZ;
			int y = normalY == 0 ? (normalX != 0 ? crossU : crossV) : normalY;

			vertex(bf, scale, x, y, z, u, v, vertexU, vertexV, color);
		}

		tess.draw();

	}

	private static void vertex(BufferBuilder bf, double scale, int xDir, int yDir, int zDir, double startU, double startV, double uAmount, double vAmount, Color color) {
		bf.pos(scale * xDir, scale * yDir, scale * zDir)
				.tex((startU + uAmount) * TX, (startV + vAmount) * TX)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
				.endVertex();
	}

	public static void renderCube(double scale, Color color) {
		Tessellator tess = Tessellator.getInstance();

		side(tess, scale, color, EnumFacing.DOWN, 2, 0);
		side(tess, scale, color, EnumFacing.UP, 1, 0);
		side(tess, scale, color, EnumFacing.NORTH, 1, 1);
		side(tess, scale, color, EnumFacing.SOUTH, 3, 1);
		side(tess, scale, color, EnumFacing.WEST, 2, 1);
		side(tess, scale, color, EnumFacing.EAST, 0, 1);
	}
}
