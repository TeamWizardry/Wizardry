package com.teamwizardry.wizardry.api.structure;

import com.google.common.collect.HashMultimap;
import com.teamwizardry.librarianlib.features.kotlin.ClientUtilMethods;
import com.teamwizardry.librarianlib.features.structure.Structure;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.util.EnumMap;

public class CachedStructure extends Structure {

	public final String name;
	@SideOnly(Side.CLIENT)
	public HashMultimap<BlockRenderLayer, Template.BlockInfo> blocks;
	@SideOnly(Side.CLIENT)
	public EnumMap<BlockRenderLayer, int[]> vboCaches;
	public Vec3d perfectCenter;

	public CachedStructure(@NotNull String name, @Nullable IBlockAccess access) {
		super(new ResourceLocation(Wizardry.MODID, name));

		this.name = name;

		int minX = 0;
		int minY = 0;
		int minZ = 0;
		int maxX = 0;
		int maxY = 0;
		int maxZ = 0;
		if (getTemplateBlocks() != null)
			for (Template.BlockInfo info : getTemplateBlocks()) {
				BlockPos pos = info.pos;

				minX = Math.min(minX, pos.getX());
				minY = Math.min(minY, pos.getY());
				minZ = Math.min(minZ, pos.getZ());

				maxX = Math.max(maxX, pos.getX());
				maxY = Math.max(maxY, pos.getY());
				maxZ = Math.max(maxZ, pos.getZ());
			}

		BlockPos max = new BlockPos(maxX, maxY, maxZ);
		BlockPos min = new BlockPos(minX, minY, minZ);

		BlockPos size = max.subtract(min);

		perfectCenter = new Vec3d(
				size.getX() / 2.0,
				size.getY() / 2.0,
				size.getZ() / 2.0);
		setOrigin(new BlockPos(perfectCenter));

		ClientRunnable.run(new ClientRunnable() {
			@SideOnly(Side.CLIENT)
			@Override
			public void runIfClient() {
				blocks = HashMultimap.create();
				vboCaches = new EnumMap<>(BlockRenderLayer.class);

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
						IBlockAccess blockAccess = access == null ? getBlockAccess() : access;

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
				}
			}
		});
	}

	@SideOnly(Side.CLIENT)
	public void draw() {
		for (BlockRenderLayer layer : blocks.keySet()) {
			Tessellator tes = Tessellator.getInstance();
			BufferBuilder buffer = tes.getBuffer();
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
			buffer.addVertexData(vboCaches.get(layer));
			tes.draw();
		}
	}
}
