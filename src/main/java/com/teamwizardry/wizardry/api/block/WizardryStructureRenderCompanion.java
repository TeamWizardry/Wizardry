package com.teamwizardry.wizardry.api.block;

import com.google.common.collect.HashMultimap;
import com.teamwizardry.librarianlib.features.kotlin.ClientUtilMethods;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WizardryStructureRenderCompanion {

	private HashMultimap<Integer, Template.BlockInfo> blocks = HashMultimap.create();
	private HashMap<Integer, int[]> vboCaches = new HashMap<>();

	private boolean builtVBO = false;
	private IBlockAccess access = null;
	private Vec3i offset = null;
	private ResourceLocation loc;
	private WizardryStructure structure;

	public WizardryStructureRenderCompanion(ResourceLocation location) {
		this.loc = location;
	}

	@Nonnull
	public List<Template.BlockInfo> getBlockInfos() {
		if (getOrMakeStructure() == null) return new ArrayList<>();

		return structure.blockInfos();
	}

	@Nullable
	private WizardryStructure getOrMakeStructure() {
		if (structure == null) {
			Block block = ForgeRegistries.BLOCKS.getValue(loc);
			if (!(block instanceof IStructure) || block.getRegistryName() == null) return null;

			structure = new WizardryStructure(block.getRegistryName());
			offset = ((IStructure) block).offsetToCenter();
		}

		return structure;
	}

	@SideOnly(Side.CLIENT)
	public void draw(IBlockAccess access, float alpha) {
		if (getOrMakeStructure() == null) return;

		if (!builtVBO || this.access != access) {
			blocks = HashMultimap.create();
			vboCaches.clear();

			if (structure.sudoGetTemplateBlocks() == null) return;

			for (Template.BlockInfo info : structure.sudoGetTemplateBlocks()) {
				if (info.blockState.getMaterial() == Material.AIR) continue;
				if (info.blockState.getRenderType() == EnumBlockRenderType.INVISIBLE) continue;
				blocks.put(info.blockState.getBlock().getRenderLayer().ordinal(), info);
			}

			for (int layerID : blocks.keySet()) {
				Tessellator tes = Tessellator.getInstance();
				BufferBuilder buffer = tes.getBuffer();
				BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();

				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

				for (Template.BlockInfo info : blocks.get(layerID)) {
					IBlockAccess blockAccess = access != null ? access : structure.getBlockAccess();

					buffer.setTranslation(info.pos.getX(), info.pos.getY(), info.pos.getZ());

					if (info.blockState.getBlock() != ModBlocks.MANA_BATTERY) {
						dispatcher.getBlockModelRenderer().renderModel(blockAccess, dispatcher.getModelForState(info.blockState), info.blockState, BlockPos.ORIGIN, buffer, true);
					} else {
						try {
							IModel model = ModelLoaderRegistry.getModel(new ResourceLocation(Wizardry.MODID, "block/mana_crystal"));
							IBakedModel battery = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
									location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));

							dispatcher.getBlockModelRenderer().renderModel(blockAccess, battery, info.blockState, BlockPos.ORIGIN, buffer, false);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					buffer.setTranslation(0, 0, 0);
				}

				vboCaches.put(layerID, ClientUtilMethods.createCacheArrayAndReset(buffer));
				builtVBO = true;
				this.access = access;
			}

		} else {

			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GlStateManager.enablePolygonOffset();
			GlStateManager.doPolygonOffset(1f, -0.05f);

			Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			GlStateManager.translate(-offset.getX(), -offset.getY(), -offset.getZ());


			for (int layerID : blocks.keySet()) {
				Tessellator tes = Tessellator.getInstance();
				BufferBuilder buffer = tes.getBuffer();
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
				buffer.addVertexData(vboCaches.get(layerID));

				for (int i = 0; i < buffer.getVertexCount(); i++) {
					int idx = buffer.getColorIndex(i + 1);
					buffer.putColorRGBA(idx, 255, 255, 255, (int) (alpha * 255));
				}

				tes.draw();
			}

			GlStateManager.disablePolygonOffset();
			GlStateManager.color(1F, 1F, 1F, 1F);
			GlStateManager.enableDepth();
			GlStateManager.popMatrix();
		}
	}
}
