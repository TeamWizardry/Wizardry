package com.teamwizardry.wizardry.api.book.structure;

import com.google.common.collect.HashMultimap;
import com.teamwizardry.librarianlib.features.kotlin.ClientUtilMethods;
import com.teamwizardry.librarianlib.features.structure.Structure;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.util.EnumMap;

public class CachedStructure extends Structure {

	public final ResourceLocation name;
	public Vec3d perfectCenter;
	@SideOnly(Side.CLIENT)
	private HashMultimap<BlockRenderLayer, Template.BlockInfo> blocks;
	@SideOnly(Side.CLIENT)
	private EnumMap<BlockRenderLayer, int[]> vboCaches;
	private IBlockAccess access;
	private boolean builtVbos = false;

	public CachedStructure(@NotNull ResourceLocation name, @Nullable IBlockAccess access) {
		super(name);

		this.access = access;
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
	}

	@SideOnly(Side.CLIENT)
	public void draw() {
		if (!builtVbos) {
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

					dispatcher.getBlockModelRenderer().renderModel(blockAccess, dispatcher.getModelForState(info.blockState), info.blockState, BlockPos.ORIGIN, buffer, true);

					buffer.setTranslation(0, 0, 0);
				}

				vboCaches.put(layer, ClientUtilMethods.createCacheArrayAndReset(buffer));
			}

			builtVbos = true;
		}

		for (BlockRenderLayer layer : blocks.keySet()) {
			Tessellator tes = Tessellator.getInstance();
			BufferBuilder buffer = tes.getBuffer();
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
			buffer.addVertexData(vboCaches.get(layer));
			tes.draw();
		}
	}
}
