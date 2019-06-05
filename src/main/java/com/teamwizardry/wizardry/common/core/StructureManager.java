package com.teamwizardry.wizardry.common.core;

import com.google.common.collect.HashMultimap;
import com.teamwizardry.librarianlib.features.kotlin.ClientUtilMethods;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

public class StructureManager {

	private final HashMap<ResourceLocation, WizardryStructure> structures = new HashMap<>();
	private final HashMap<ResourceLocation, HashMap<Integer, int[]>> vboCache = new HashMap<>();

	public StructureManager() {
	}

	@Nullable
	public WizardryStructure getStructure(ResourceLocation location) {
		return structures.get(location);
	}

	/**
	 * Add a structure from it's resourcelocation. Note that the structure will not be baked in the renderer
	 * until the ModelBakeEvent is called (resource reloading) or bake() is manually called.
	 *
	 * @param location The location of the structure.nbt file.
	 */
	public void addStructure(ResourceLocation location, BlockPos origin) {
		if (structures.containsKey(location)) return;

		WizardryStructure structure = new WizardryStructure(location);
		structure.setOrigin(origin);
		structures.put(location, structure);
		bake(location);
	}

	/**
	 * Will not draw the structure if it is not already baked. Resource reloading will re-bake if required.
	 *
	 * @param alpha    The transparency of the rendered structure.
	 * @param location The ResourceLocation of the structure to look up.
	 */
	@SideOnly(Side.CLIENT)
	public void draw(ResourceLocation location, float alpha) {
		HashMap<Integer, int[]> cache = vboCache.get(location);

		//	if (cache == null || cache.isEmpty()) {
		//		bake(location);
		//		return;
		//	}

		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.enablePolygonOffset();
		GlStateManager.doPolygonOffset(1f, -0.05f);
		//	GlStateManager.disableDepth();

		int alphaFunc = GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC);
		float alphaRef = GL11.glGetFloat(GL11.GL_ALPHA_TEST_REF);
		GlStateManager.alphaFunc(GL11.GL_ALWAYS, 1);

		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		Tessellator tes = Tessellator.getInstance();
		BufferBuilder buffer = tes.getBuffer();

		for (int layerID : cache.keySet()) {

			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
			buffer.addVertexData(cache.get(layerID));

			for (int i = 0; i < buffer.getVertexCount(); i++) {
				buffer.putColorRGBA(buffer.getColorIndex(i), 255, 255, 255, (int) (alpha * 255));
			}

			tes.draw();
		}

		GlStateManager.alphaFunc(alphaFunc, alphaRef);
		//	GlStateManager.enableDepth();
		GlStateManager.disablePolygonOffset();
		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.popMatrix();
	}

	/**
	 * Called automatically in bake event. If baking again is required, you can use this.
	 */
	@SideOnly(Side.CLIENT)
	public void bake(ResourceLocation resourceLocation) {
		Wizardry.LOGGER.info("Attempting to bake structure \"" + resourceLocation.toString() + "\"");
		WizardryStructure structure = structures.get(resourceLocation);
		if (structure == null) {
			Wizardry.LOGGER.error("Could not bake structure \"" + resourceLocation.toString() + "\". Does not seem to exist?");
			return;
		}

		HashMultimap<Integer, Template.BlockInfo> blocks = HashMultimap.create();
		for (Template.BlockInfo info : structure.blockInfos()) {
			if (info.blockState.getMaterial() == Material.AIR) continue;
			if (info.blockState.getRenderType() == EnumBlockRenderType.INVISIBLE) continue;
			blocks.put(info.blockState.getBlock().getRenderLayer().ordinal(), info);
		}

		BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
		// It CAN be null.
		//noinspection ConstantConditions
		if (dispatcher == null) {
			Wizardry.LOGGER.error("Could not bake structure \"" + resourceLocation.toString() + "\". Dispatcher is null. Don't call bake so early?");
			return;
		}
		Tessellator tes = Tessellator.getInstance();
		BufferBuilder bb = tes.getBuffer();

		HashMap<Integer, int[]> cache = new HashMap<>();
		for (int layerID : blocks.keySet()) {

			bb.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

			for (Template.BlockInfo info : blocks.get(layerID)) {
				if (info.blockState == null) continue;
				if (info.blockState.getMaterial() == Material.AIR) continue;
				if (info.blockState.getRenderType() == EnumBlockRenderType.INVISIBLE) continue;

				bb.setTranslation(info.pos.getX() - structure.getOrigin().getX(), info.pos.getY() - structure.getOrigin().getY(), info.pos.getZ() - structure.getOrigin().getZ());

				dispatcher.renderBlock(info.blockState, BlockPos.ORIGIN, structure.getWizardryAccess(), bb);

				bb.setTranslation(0, 0, 0);
			}

			cache.put(layerID, ClientUtilMethods.createCacheArrayAndReset(bb));
			bb.reset();
		}
		vboCache.put(resourceLocation, cache);

		Wizardry.LOGGER.info("Baking for structure \"" + resourceLocation.toString() + "\" completed.");
	}

	/**
	 * Called automatically in resource reload event. If baking again is required, you can use this.
	 */
	@SideOnly(Side.CLIENT)
	public void bake() {
		vboCache.clear();

		for (ResourceLocation resourceLocation : structures.keySet()) {
			bake(resourceLocation);
		}
	}
}
