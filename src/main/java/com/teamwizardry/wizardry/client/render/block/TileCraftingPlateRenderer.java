package com.teamwizardry.wizardry.client.render.block;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.animator.Animator;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpBezier3D;
import com.teamwizardry.librarianlib.features.tesr.TileRenderHandler;
import com.teamwizardry.wizardry.api.block.CachedStructure;
import com.teamwizardry.wizardry.api.block.IStructure;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.render.ClusterObject;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RandUtilSeed;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

/**
 * Created by Saad on 6/11/2016.
 */
public class TileCraftingPlateRenderer extends TileRenderHandler<TileCraftingPlate> {

	private static Animator animator = new Animator();

	private CachedStructure cachedStructure;
	private RandUtilSeed rand;

	public TileCraftingPlateRenderer(@NotNull TileCraftingPlate tile) {
		super(tile);

		animator = new Animator();
		cachedStructure = new CachedStructure(((IStructure) tile.getBlockType()).getStructure().loc, tile.getWorld());
		rand = new RandUtilSeed(RandUtil.nextLong(100, 100000));
	}

	@Override
	public void render(float partialTicks, int destroyStage, float alpha) {
		if (tile.revealStructure && tile.getBlockType() instanceof IStructure && !((IStructure) tile.getBlockType()).isStructureComplete(tile.getWorld(), tile.getPos())) {

			IStructure structure = ((IStructure) tile.getBlockType());

			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GlStateManager.enablePolygonOffset();
			GlStateManager.doPolygonOffset(1f, -0.05f);

			GlStateManager.translate(-structure.offsetToCenter().getX(), -structure.offsetToCenter().getY(), -structure.offsetToCenter().getZ());
			Minecraft mc = Minecraft.getMinecraft();
			Tessellator tes = Tessellator.getInstance();
			BufferBuilder buffer = tes.getBuffer();

			mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

			for (BlockRenderLayer layer : cachedStructure.blocks.keySet()) {
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
				buffer.addVertexData(cachedStructure.vboCaches.get(layer));

				for (int i = 0; i < buffer.getVertexCount(); i++) {
					int idx = buffer.getColorIndex(i + 1);
					buffer.putColorRGBA(idx, 255, 255, 255, 200);
				}
				tes.draw();
			}

			GlStateManager.disablePolygonOffset();
			GlStateManager.color(1F, 1F, 1F, 1F);
			GlStateManager.enableDepth();
			GlStateManager.popMatrix();
			return;
		}

		ItemStack pearl = tile.inputPearl.getHandler().getStackInSlot(0);
		CapManager manager = new CapManager(pearl);

		int count = 0;
		for (int i = 0; i < tile.realInventory.getHandler().getSlots(); i++) {
			if (!tile.realInventory.getHandler().getStackInSlot(i).isEmpty()) {
				count++;
			}
		}

		for (int i = 0; i < tile.realInventory.getHandler().getSlots(); i++) {
			if (!tile.realInventory.getHandler().getStackInSlot(i).isEmpty()) {
				ItemStack stack = tile.realInventory.getHandler().getStackInSlot(i);

				ClusterObject cluster = tile.renders[i];
				if (cluster == null) continue;

				cluster.tick(tile.getWorld(), tile.random);

				double timeDifference = (tile.getWorld().getTotalWorldTime() - cluster.worldTime + partialTicks) / cluster.destTime;
				Vec3d current = cluster.origin.add(cluster.dest.subtract(cluster.origin).scale(MathHelper.sin((float) (timeDifference * Math.PI / 2))));

				if (!manager.isManaEmpty()) {
					if (tile.inputPearl.getHandler().getStackInSlot(0).isEmpty() && RandUtil.nextInt(count > 0 && count / 2 > 0 ? count / 2 : 1) == 0)
						LibParticles.CLUSTER_DRAPE(tile.getWorld(), new Vec3d(tile.getPos()).addVector(0.5, 0.5, 0.5).add(current));

					if (!tile.inputPearl.getHandler().getStackInSlot(0).isEmpty()) {
						if (RandUtil.nextInt(count > 0 && count / 4 > 0 ? count / 4 : 1) == 0) {
							LibParticles.CRAFTING_ALTAR_CLUSTER_SUCTION(tile.getWorld(), new Vec3d(tile.getPos()).addVector(0.5, 0.75, 0.5), new InterpBezier3D(current, new Vec3d(0, 0, 0)));
						}
					}
				}


				GlStateManager.pushMatrix();
				GlStateManager.translate(0.5 + current.x, 0.5 + current.y, 0.5 + current.z);
				GlStateManager.scale(0.3, 0.3, 0.3);
				GlStateManager.rotate((tile.getWorld().getTotalWorldTime()) + ClientTickHandler.getPartialTicks(), 0, 1, 0);
				Minecraft.getMinecraft().getRenderItem().renderItem(stack, TransformType.NONE);
				GlStateManager.popMatrix();
			}
		}

		//if (!manager.isManaEmpty() && tile.isCrafting && (tile.output != null)) {
		//	LibParticles.CRAFTING_ALTAR_HELIX(tile.getWorld(), new Vec3d(tile.getPos()).addVector(0.5, 0.25, 0.5));
		//}

		if (!tile.outputPearl.getHandler().getStackInSlot(0).isEmpty()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.5, 1, 0.5);
			GlStateManager.scale(0.4, 0.4, 0.4);
			GlStateManager.rotate(tile.getWorld().getTotalWorldTime(), 0, 1, 0);
			Minecraft.getMinecraft().getRenderItem().renderItem(tile.outputPearl.getHandler().getStackInSlot(0), TransformType.NONE);
			GlStateManager.popMatrix();
		} else if (!manager.isManaEmpty() && RandUtil.nextInt(4) == 0) {
			LibParticles.CRAFTING_ALTAR_IDLE(tile.getWorld(), new Vec3d(tile.getPos()).addVector(0.5, 0.7, 0.5));
		}
	}
}
