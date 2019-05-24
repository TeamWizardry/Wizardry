package com.teamwizardry.wizardry.api.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.client.model.pipeline.QuadGatheringTransformer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class RenderUtils {


	/**
	 * Reimplement vanilla item so we can draw pearl stacks with opacity support.
	 */
	private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
	/**
	 * Reimplement vanilla item so we can draw pearl stacks with opacity support.
	 */
	private static final LightGatheringTransformer lightGatherer = new LightGatheringTransformer();

	/**
	 * Reimplement vanilla item so we can draw pearl stacks with opacity support.
	 */
	private static void renderModel(IBakedModel model, int color, ItemStack stack) {
		renderLitItem(Minecraft.getMinecraft().getRenderItem(), model, color, stack);
	}


	/**
	 * Will render an itemstack with opacity support properly. Minecraft does not normally support this so we
	 * reimplemented all the itemstack rendering code.
	 */
	public static void renderItemStackWithOpacity(ItemStack stack, float opacity, @Nullable Runnable preDrawRunnable) {
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableTexture2D();

		GlStateManager.scale(2, 2, 2);

		if (preDrawRunnable != null) preDrawRunnable.run();

		GlStateManager.color(1f, 1f, 1f, opacity);

		IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, null, Minecraft.getMinecraft().player);

		GlStateManager.pushMatrix();
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

		GlStateManager.translate(8.0F, 8.0F, 0.0F);
		GlStateManager.scale(1.0F, -1.0F, 1.0F);
		GlStateManager.scale(16.0F, 16.0F, 16.0F);

		if (model.isGui3d()) GlStateManager.enableLighting();
		else GlStateManager.disableLighting();

		ForgeHooksClient.handleCameraTransforms(model, ItemCameraTransforms.TransformType.GUI, false);

		if (!stack.isEmpty()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(-0.5F, -0.5F, -0.5F);

			if (model.isBuiltInRenderer()) {
				GlStateManager.enableRescaleNormal();
				stack.getItem().getTileEntityItemStackRenderer().renderByItem(stack);
			} else {
				renderModel(model, 0xFFFFFF | (((int) (opacity * 255)) << 24), stack);

				if (stack.hasEffect()) {
					renderEffect(model);
				}
			}

			GlStateManager.translate(0.5F, 0.5F, 0.5F);
			GlStateManager.popMatrix();
		}

		GlStateManager.translate(-8.0F, -8.0F, -0.0F);
		GlStateManager.scale(1 / 16.0F, 1 / 16.0F, 1 / 16.0F);
		GlStateManager.scale(1 / 2.0, 1 / 2.0, 1 / 2.0);

		GlStateManager.disableAlpha();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableLighting();
		GlStateManager.popMatrix();

		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
		GlStateManager.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
	}

	/**
	 * Reimplement vanilla item so we can draw pearl stacks with opacity support.
	 */
	private static int getColorMultiplier(ItemStack stack, int tintIndex) {
		if (tintIndex == -1 || stack.isEmpty()) return 0xFFFFFFFF;

		int colorMultiplier = Minecraft.getMinecraft().getItemColors().colorMultiplier(stack, tintIndex);

		if (EntityRenderer.anaglyphEnable) {
			colorMultiplier = TextureUtil.anaglyphColor(colorMultiplier);
		}

		// FUCK YOU
		//	// Always full opacity
		//	colorMultiplier |= 0xff << 24; // -16777216

		return colorMultiplier;
	}


	/**
	 * Reimplement vanilla item so we can draw pearl stacks with opacity support.
	 */
	private static void renderLitItem(RenderItem ri, IBakedModel model, int color, ItemStack stack) {
		List<BakedQuad> allquads = new ArrayList<>();

		for (EnumFacing enumfacing : EnumFacing.VALUES) {
			allquads.addAll(model.getQuads(null, enumfacing, 0));
		}

		allquads.addAll(model.getQuads(null, null, 0));

		if (allquads.isEmpty()) return;

		// Current list of consecutive quads with the same lighting
		List<BakedQuad> segment = new ArrayList<>();

		// Lighting of the current segment
		int segmentBlockLight = -1;
		int segmentSkyLight = -1;
		// Coloring of the current segment
		int segmentColorMultiplier = color;
		// If the current segment contains lighting data
		boolean hasLighting = false;

		// Tint index cache to avoid unnecessary IItemColor lookups
		int prevTintIndex = -1;

		for (int i = 0; i < allquads.size(); i++) {
			BakedQuad q = allquads.get(i);

			// Lighting of the current quad
			int bl = 0;
			int sl = 0;

			// Fail-fast on ITEM, as it cannot have light data
			if (q.getFormat() != DefaultVertexFormats.ITEM && q.getFormat().hasUvOffset(1)) {
				q.pipe(lightGatherer);
				if (lightGatherer.hasLighting()) {
					bl = lightGatherer.blockLight;
					sl = lightGatherer.skyLight;
				}
			}

			int colorMultiplier = segmentColorMultiplier;

			// If there is no color override, and this quad is tinted, we need to apply IItemColor
			if (color == 0xFFFFFFFF && q.hasTintIndex()) {
				int tintIndex = q.getTintIndex();

				if (prevTintIndex != tintIndex) {
					colorMultiplier = getColorMultiplier(stack, tintIndex);
				}
				prevTintIndex = tintIndex;
			} else {
				colorMultiplier = color;
				prevTintIndex = -1;
			}

			boolean lightingDirty = segmentBlockLight != bl || segmentSkyLight != sl;
			boolean colorDirty = hasLighting && segmentColorMultiplier != colorMultiplier;

			// If lighting or color data has changed, draw the segment and flush it
			if (lightingDirty || colorDirty) {
				if (i > 0) // Make sure this isn't the first quad being processed
				{
					drawSegment(color, stack, segment, segmentBlockLight, segmentSkyLight, segmentColorMultiplier, lightingDirty && (hasLighting || segment.size() < i), colorDirty);
				}
				segmentBlockLight = bl;
				segmentSkyLight = sl;
				segmentColorMultiplier = colorMultiplier;
				hasLighting = segmentBlockLight > 0 || segmentSkyLight > 0;
			}

			segment.add(q);
		}

		drawSegment(color, stack, segment, segmentBlockLight, segmentSkyLight, segmentColorMultiplier, hasLighting || segment.size() < allquads.size(), false);

		// Clean up render state if necessary
		if (hasLighting) {
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, OpenGlHelper.lastBrightnessX, OpenGlHelper.lastBrightnessY);
			GL11.glMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_EMISSION, RenderHelper.setColorBuffer(0, 0, 0, 1));
		}
	}

	/**
	 * Reimplement vanilla item so we can draw pearl stacks with opacity support.
	 */
	private static void drawSegment(int baseColor, ItemStack stack, List<BakedQuad> segment, int bl, int sl, int tintColor, boolean updateLighting, boolean updateColor) {
		BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);

		float lastBl = OpenGlHelper.lastBrightnessX;
		float lastSl = OpenGlHelper.lastBrightnessY;

		if (updateLighting || updateColor) {
			float emissive = Math.max(bl, sl) / 240f;

			float r = (tintColor >>> 16 & 0xff) / 255f;
			float g = (tintColor >>> 8 & 0xff) / 255f;
			float b = (tintColor & 0xff) / 255f;

			GL11.glMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_EMISSION, RenderHelper.setColorBuffer(emissive * r, emissive * g, emissive * b, 1));

			if (updateLighting) {
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, Math.max(bl, lastBl), Math.max(sl, lastSl));
			}
		}

		renderQuads(bufferbuilder, segment, baseColor, stack);
		Tessellator.getInstance().draw();

		// Preserve this as it represents the "world" lighting
		OpenGlHelper.lastBrightnessX = lastBl;
		OpenGlHelper.lastBrightnessY = lastSl;

		segment.clear();
	}

	/**
	 * Reimplement vanilla item so we can draw pearl stacks with opacity support.
	 * + FIX THE OPACITY RENDERING.
	 */
	private static void renderQuads(BufferBuilder renderer, List<BakedQuad> quads, int color, ItemStack stack) {
		color &= 0xFF000000;
		boolean flag = !stack.isEmpty();
		int i = 0;

		for (int j = quads.size(); i < j; ++i) {
			BakedQuad bakedquad = quads.get(i);
			int k = color | 0xFFFFFF;

			if (flag && bakedquad.hasTintIndex()) {
				k = Minecraft.getMinecraft().getItemColors().colorMultiplier(stack, bakedquad.getTintIndex());

				if (EntityRenderer.anaglyphEnable) {
					k = TextureUtil.anaglyphColor(k);
				}

				k &= 0xFFFFFF;
				k |= color;
			}

			LightUtil.renderQuadColor(renderer, bakedquad, k);
		}
	}

	/**
	 * Reimplement vanilla item so we can draw pearl stacks with opacity support.
	 */
	private static void renderEffect(IBakedModel model) {
		GlStateManager.depthMask(false);
		GlStateManager.depthFunc(514);
		GlStateManager.disableLighting();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
		Minecraft.getMinecraft().getTextureManager().bindTexture(RES_ITEM_GLINT);
		GlStateManager.matrixMode(5890);
		GlStateManager.pushMatrix();
		GlStateManager.scale(8.0F, 8.0F, 8.0F);
		float f = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
		GlStateManager.translate(f, 0.0F, 0.0F);
		GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
		renderModel(model, -8372020, ItemStack.EMPTY);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GlStateManager.scale(8.0F, 8.0F, 8.0F);
		float f1 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F / 8.0F;
		GlStateManager.translate(-f1, 0.0F, 0.0F);
		GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
		renderModel(model, -8372020, ItemStack.EMPTY);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableLighting();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		GlStateManager.scale(1 / 8.0F, 1 / 8.0F, 1 / 8.0F);
	}

	/**
	 * Reimplement vanilla item so we can draw pearl stacks with opacity support.
	 */
	private static class LightGatheringTransformer extends QuadGatheringTransformer {

		private static final VertexFormat FORMAT = new VertexFormat().addElement(DefaultVertexFormats.TEX_2F).addElement(DefaultVertexFormats.TEX_2S);

		int blockLight, skyLight;

		{
			setVertexFormat(FORMAT);
		}

		boolean hasLighting() {
			return dataLength[1] >= 2;
		}

		@Override
		protected void processQuad() {
			// Reset light data
			blockLight = 0;
			skyLight = 0;
			// Compute average light for all 4 vertices
			for (int i = 0; i < 4; i++) {
				blockLight += (int) ((quadData[1][i][0] * 0xFFFF) / 0x20);
				skyLight += (int) ((quadData[1][i][1] * 0xFFFF) / 0x20);
			}
			// Values must be multiplied by 16, divided by 4 for average => x4
			blockLight *= 4;
			skyLight *= 4;
		}

		// Dummy overrides

		@Override
		public void setQuadTint(int tint) {
		}

		@Override
		public void setQuadOrientation(@NotNull EnumFacing orientation) {
		}

		@Override
		public void setApplyDiffuseLighting(boolean diffuse) {
		}

		@Override
		public void setTexture(@NotNull TextureAtlasSprite texture) {
		}
	}

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
