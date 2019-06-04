package com.teamwizardry.wizardry.common.core;

import com.google.common.collect.HashMultimap;
import com.teamwizardry.librarianlib.features.kotlin.ClientUtilMethods;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.SelectiveReloadStateHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.function.Predicate;

public class StructureManager implements ISelectiveResourceReloadListener {

	private final HashMap<ResourceLocation, WizardryStructure> structures = new HashMap<>();
	private final HashMap<ResourceLocation, HashMap<Integer, int[]>> vboCache = new HashMap<>();

	public StructureManager() {
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
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
		bake(location);
		HashMap<Integer, int[]> cache = vboCache.get(location);
		if (cache == null) return;

		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.enablePolygonOffset();
		GlStateManager.doPolygonOffset(1f, -0.05f);

		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		Tessellator tes = Tessellator.getInstance();
		BufferBuilder buffer = tes.getBuffer();

		for (int layerID : cache.keySet()) {

			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
			buffer.addVertexData(cache.get(layerID));

			for (int i = 0; i < buffer.getVertexCount(); i++) {
				int idx = buffer.getColorIndex(i + 1);
				buffer.putColorRGBA(idx, 255, 255, 255, (int) (alpha * 255));
			}

			tes.draw();
		}

		GlStateManager.disablePolygonOffset();
		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.popMatrix();
	}

	/**
	 * Called automatically in bake event. If baking again is required, you can use this.
	 */
	@SideOnly(Side.CLIENT)
	public void bake(ResourceLocation resourceLocation) {
		WizardryStructure structure = structures.get(resourceLocation);

		HashMultimap<Integer, Template.BlockInfo> blocks = HashMultimap.create();

		BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
		Tessellator tes = Tessellator.getInstance();
		BufferBuilder bb = tes.getBuffer();

		bb.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		HashMap<Integer, int[]> cache = new HashMap<>();
		for (int layerID : blocks.keySet()) {
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

	@Override
	public void onResourceManagerReload(@NotNull IResourceManager resourceManager) {
		onResourceManagerReload(resourceManager, SelectiveReloadStateHandler.INSTANCE.get());
	}

	@Override
	public void onResourceManagerReload(@NotNull IResourceManager resourceManager, @NotNull Predicate<IResourceType> resourcePredicate) {
		bake();
	}
}
