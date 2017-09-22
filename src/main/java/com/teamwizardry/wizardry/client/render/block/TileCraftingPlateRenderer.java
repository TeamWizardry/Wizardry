package com.teamwizardry.wizardry.client.render.block;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpBezier3D;
import com.teamwizardry.wizardry.api.block.IStructure;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.render.ClusterObject;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.client.core.IsolatedBlock;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.structure.template.Template;
import org.lwjgl.opengl.GL11;

import java.util.EnumMap;
import java.util.HashSet;

/**
 * Created by Saad on 6/11/2016.
 */
public class TileCraftingPlateRenderer extends TileEntitySpecialRenderer<TileCraftingPlate> {

	@Override
	public void render(TileCraftingPlate te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (te.getBlockType() instanceof IStructure && Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() == ModItems.MAGIC_WAND) {
			IStructure structure = ((IStructure) te.getBlockType());

			GlStateManager.pushMatrix();
			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
			GlStateManager.enableCull();

			GlStateManager.translate(x, y, z);
			GlStateManager.translate(-structure.offsetToCenter().getX(), -structure.offsetToCenter().getY(), -structure.offsetToCenter().getZ());
			Minecraft mc = Minecraft.getMinecraft();
			Tessellator tes = Tessellator.getInstance();
			BufferBuilder buffer = tes.getBuffer();
			BlockRendererDispatcher dispatcher = mc.getBlockRendererDispatcher();

			IsolatedBlock block = new IsolatedBlock(te.getWorld().getBlockState(te.getPos()), null);
			mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			EnumMap<BlockRenderLayer, HashSet<Template.BlockInfo>> blocks = new EnumMap<>(BlockRenderLayer.class);
			for (Template.BlockInfo info : ((IStructure) te.getBlockType()).getStructure().blockInfos()) {

				if (info.blockState.getBlock() == Blocks.AIR) continue;
				if (info.blockState.getBlock() == te.getWorld().getBlockState(te.getPos().add(-structure.offsetToCenter().getX(), -structure.offsetToCenter().getY(), -structure.offsetToCenter().getZ()).add(info.pos)).getBlock())
					continue;

				HashSet<Template.BlockInfo> set = blocks.get(info.blockState.getBlock().getBlockLayer());
				if (set == null) set = new HashSet<>();
				set.add(info);
				blocks.put(info.blockState.getBlock().getBlockLayer(), set);
			}
			for (BlockRenderLayer layer : blocks.keySet()) {
				for (Template.BlockInfo info : blocks.get(layer)) {
					GlStateManager.translate(info.pos.getX(), info.pos.getY(), info.pos.getZ());
					buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
					dispatcher.renderBlock(info.blockState, BlockPos.ORIGIN, block, buffer);
					for (int i = 0; i < buffer.getVertexCount(); i++) {
						int idx = buffer.getColorIndex(i + 1);
						buffer.putColorRGBA(idx, 255, 255, 255, 150);
					}
					tes.draw();
					GlStateManager.translate(-info.pos.getX(), -info.pos.getY(), -info.pos.getZ());
				}
			}

			GlStateManager.disableAlpha();
			GlStateManager.disableBlend();
			GlStateManager.disableCull();
			GlStateManager.popMatrix();
			return;
		}

		CapManager manager = new CapManager(te.cap);

		int count = te.inventory.size();
		for (ClusterObject cluster : te.inventory) {
			double timeDifference = (te.getWorld().getTotalWorldTime() - cluster.worldTime + partialTicks) / cluster.destTime;
			Vec3d current = cluster.origin.add(cluster.dest.subtract(cluster.origin).scale(MathHelper.sin((float) (timeDifference * Math.PI / 2))));

			if (!manager.isManaEmpty()) {
				if (!te.isCrafting && RandUtil.nextInt(count > 0 && count / 2 > 0 ? count / 2 : 1) == 0)
					LibParticles.CLUSTER_DRAPE(te.getWorld(), new Vec3d(te.getPos()).addVector(0.5, 0.5, 0.5).add(current));

				if (te.isCrafting && (te.output != null)) {
					if (RandUtil.nextInt(count > 0 && count / 4 > 0 ? count / 4 : 1) == 0) {
						LibParticles.CRAFTING_ALTAR_CLUSTER_SUCTION(te.getWorld(), new Vec3d(te.getPos()).addVector(0.5, 0.75, 0.5), new InterpBezier3D(current, new Vec3d(0, 0, 0)));
					}
				}
			}

			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5 + current.x, y + 0.5 + current.y, z + 0.5 + current.z);
			GlStateManager.scale(0.3, 0.3, 0.3);
			GlStateManager.rotate((cluster.tick) + ClientTickHandler.getPartialTicks(), 0, 1, 0);
			Minecraft.getMinecraft().getRenderItem().renderItem(cluster.stack, TransformType.NONE);
			GlStateManager.popMatrix();
			//Minecraft.getMinecraft().player.sendChatMessage((cluster.stack.hashCode()) / 100000000.0 + "");
		}

		//if (!manager.isManaEmpty() && te.isCrafting && (te.output != null)) {
		//	LibParticles.CRAFTING_ALTAR_HELIX(te.getWorld(), new Vec3d(te.getPos()).addVector(0.5, 0.25, 0.5));
		//}

		if (!te.isCrafting && (te.output != null)) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5, y + 1, z + 0.5);
			GlStateManager.scale(0.4, 0.4, 0.4);
			GlStateManager.rotate(te.tick, 0, 1, 0);
			Minecraft.getMinecraft().getRenderItem().renderItem(te.output, TransformType.NONE);
			GlStateManager.popMatrix();
		} else if (!manager.isManaEmpty() && RandUtil.nextInt(4) == 0) {
			LibParticles.CRAFTING_ALTAR_IDLE(te.getWorld(), new Vec3d(te.getPos()).addVector(0.5, 0.7, 0.5));
		}
	}
}
