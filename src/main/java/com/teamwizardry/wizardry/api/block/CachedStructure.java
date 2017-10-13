package com.teamwizardry.wizardry.api.block;

import com.google.common.collect.HashMultimap;
import com.teamwizardry.librarianlib.features.kotlin.ClientUtilMethods;
import com.teamwizardry.librarianlib.features.structure.Structure;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.util.EnumMap;

public class CachedStructure extends Structure {

	private HashMultimap<BlockRenderLayer, Template.BlockInfo> blocks = HashMultimap.create();
	private EnumMap<BlockRenderLayer, int[]> vboCaches = new EnumMap<>(BlockRenderLayer.class);
	private IStructure block;

	public CachedStructure(@NotNull ResourceLocation loc) {
		super(loc);

		if (getTemplateBlocks() == null) return;

		for (Template.BlockInfo info : getTemplateBlocks()) {
			if (info.blockState.getMaterial() == Material.AIR) continue;
			blocks.put(info.blockState.getBlock().getBlockLayer(), info);
		}

		for (BlockRenderLayer layer : blocks.keySet()) {
			Tessellator tes = Tessellator.getInstance();
			BufferBuilder buffer = tes.getBuffer();
			BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();

			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

			for (Template.BlockInfo info : blocks.get(layer)) {
				buffer.setTranslation(info.pos.getX(), info.pos.getY(), info.pos.getZ());

				VoidBlockAccess blockAccess = new VoidBlockAccess(info.blockState, null);
				if (info.blockState.getBlock() != ModBlocks.MANA_BATTERY) {
					dispatcher.renderBlock(info.blockState, BlockPos.ORIGIN, blockAccess, buffer);
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

				buffer.setTranslation(-info.pos.getX(), -info.pos.getY(), -info.pos.getZ());
			}

			vboCaches.put(layer, ClientUtilMethods.createCacheArrayAndReset(buffer));
		}
	}

	public void draw() {
		for (BlockRenderLayer layer : blocks.keySet()) {
			Tessellator tes = Tessellator.getInstance();
			BufferBuilder buffer = tes.getBuffer();
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
			buffer.addVertexData(vboCaches.get(layer));
			tes.draw();
		}
	}

	public IStructure getBlock() {
		return block;
	}

	public CachedStructure setBlock(IStructure block) {
		this.block = block;
		return this;
	}
}
