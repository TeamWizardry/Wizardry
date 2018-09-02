package com.teamwizardry.wizardry.api.block;

import com.google.common.collect.HashMultimap;
import com.teamwizardry.librarianlib.features.kotlin.ClientUtilMethods;
import com.teamwizardry.librarianlib.features.structure.Structure;
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
import net.minecraft.util.BlockRenderLayer;
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
import java.util.EnumMap;
import java.util.List;

public class WizardryStructure extends Structure {

	@SideOnly(Side.CLIENT)
	private HashMultimap<BlockRenderLayer, Template.BlockInfo> blocks = HashMultimap.create();
	@SideOnly(Side.CLIENT)
	private EnumMap<BlockRenderLayer, int[]> vboCaches = new EnumMap<>(BlockRenderLayer.class);

	private boolean builtVBO = false;
	private IBlockAccess access = null;
	private Vec3i offset = null;
	private ResourceLocation loc;

	public WizardryStructure(@Nonnull ResourceLocation loc) {
		super(loc);
		this.loc = loc;
	}

	public void redraw() {
		builtVBO = false;
	}

	public List<Template.BlockInfo> sudoGetTemplateBlocks() {
		return getTemplateBlocks();
	}

	@SideOnly(Side.CLIENT)
	public void draw(IBlockAccess access, float alpha) {
		if (offset == null) {
			Block block = ForgeRegistries.BLOCKS.getValue(loc);
			if (!(block instanceof IStructure)) return;

			offset = ((IStructure) block).offsetToCenter();
		}

		if (!builtVBO || this.access != access) {
			blocks = HashMultimap.create();
			vboCaches = new EnumMap<>(BlockRenderLayer.class);

			if (getTemplateBlocks() == null) return;

			for (Template.BlockInfo info : getTemplateBlocks()) {
				if (info.blockState.getMaterial() == Material.AIR) continue;
				if (info.blockState.getRenderType() == EnumBlockRenderType.INVISIBLE) continue;
				blocks.put(info.blockState.getBlock().getRenderLayer(), info);
			}

			for (BlockRenderLayer layer : blocks.keySet()) {
				Tessellator tes = Tessellator.getInstance();
				BufferBuilder buffer = tes.getBuffer();
				BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();

				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

				for (Template.BlockInfo info : blocks.get(layer)) {
					IBlockAccess blockAccess = access != null ? access : getBlockAccess();

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

				vboCaches.put(layer, ClientUtilMethods.createCacheArrayAndReset(buffer));
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


			for (BlockRenderLayer layer : blocks.keySet()) {
				Tessellator tes = Tessellator.getInstance();
				BufferBuilder buffer = tes.getBuffer();
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
				buffer.addVertexData(vboCaches.get(layer));

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
