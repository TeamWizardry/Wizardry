package com.teamwizardry.wizardry.client.render.block;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.animator.Animator;
import com.teamwizardry.librarianlib.features.animator.Easing;
import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpBezier3D;
import com.teamwizardry.librarianlib.features.tesr.TileRenderHandler;
import com.teamwizardry.wizardry.api.block.CachedStructure;
import com.teamwizardry.wizardry.api.block.IStructure;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.util.RandUtil;
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

	private double[] angles;

	private CachedStructure cachedStructure;

	public TileCraftingPlateRenderer(@NotNull TileCraftingPlate tile) {
		super(tile);

		animator.setUseWorldTicks(true);
		cachedStructure = new CachedStructure(((IStructure) tile.getBlockType()).getStructure().loc, tile.getWorld());
		angles = new double[tile.realInventory.getHandler().getSlots()];
	}

	public void addAnimation(int i, boolean firstTime) {
		if (firstTime) {
			tile.positions[i] = Vec3d.ZERO;
			angles[i] = RandUtil.nextDouble(0, 360);
		}

		if (tile.positions[i] == null) return;

		double t;
		if (tile.inputPearl.getHandler().getStackInSlot(0).isEmpty()) t = 1;
		else {
			CapManager manager = new CapManager(tile.inputPearl.getHandler().getStackInSlot(0));
			t = 1 - (manager.getMana() / manager.getMaxMana());
		}

		double radius = RandUtil.nextDouble(5, 8) * t;

		angles[i] += RandUtil.nextDouble(-1.5, 1.5);
		double x = MathHelper.cos((float) angles[i]) * radius;
		double z = MathHelper.sin((float) angles[i]) * radius;

		Vec3d newDest = new Vec3d(x, (2 + (RandUtil.nextFloat() * 7)) * t, z);

		BasicAnimation<Vec3d[]> anim = new BasicAnimation<>(tile.positions, "[" + i + "]");
		anim.setTo(newDest);
		anim.setDuration((float) (RandUtil.nextDouble(50, 100) * t));
		anim.setEasing(Easing.easeInOutQuint);
		final int finalI = i;
		anim.setCompletion(() -> {
			if (tile.positions[finalI] == null) return;
			addAnimation(finalI, false);
		});
		animator.add(anim);
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

		// render each item at its current position
		for (int i = 0; i < tile.realInventory.getHandler().getSlots(); i++) {
			ItemStack stack = tile.realInventory.getHandler().getStackInSlot(i);
			Vec3d pos = tile.positions[i];
			if (!stack.isEmpty() && pos != null) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.5 + pos.x, 0.5 + pos.y, 0.5 + pos.z);
				GlStateManager.scale(0.3, 0.3, 0.3);
				GlStateManager.rotate((tile.getWorld().getTotalWorldTime()) + ClientTickHandler.getPartialTicks(), 0, 1, 0);
				GlStateManager.rotate((tile.getWorld().getTotalWorldTime()) + ClientTickHandler.getPartialTicks(), 1, 0, 0);
				GlStateManager.rotate((tile.getWorld().getTotalWorldTime()) + ClientTickHandler.getPartialTicks(), 0, 0, 1);
				Minecraft.getMinecraft().getRenderItem().renderItem(stack, TransformType.NONE);
				GlStateManager.popMatrix();

				if (!manager.isManaEmpty()) {
					if (tile.inputPearl.getHandler().getStackInSlot(0).isEmpty() && RandUtil.nextInt(count > 0 && count / 2 > 0 ? count / 2 : 1) == 0)
						LibParticles.CLUSTER_DRAPE(tile.getWorld(), new Vec3d(tile.getPos()).addVector(0.5, 0.5, 0.5).add(pos));

					if (!tile.inputPearl.getHandler().getStackInSlot(0).isEmpty()) {
						if (RandUtil.nextInt(count > 0 && count / 4 > 0 ? count / 4 : 1) == 0) {
							LibParticles.CRAFTING_ALTAR_CLUSTER_SUCTION(tile.getWorld(), new Vec3d(tile.getPos()).addVector(0.5, 0.75, 0.5), new InterpBezier3D(pos, new Vec3d(0, 0, 0)));
						}
					}
				}
			}
		}

		ItemStack itemStack;
		if (!tile.outputPearl.getHandler().getStackInSlot(0).isEmpty()) {
			itemStack = tile.outputPearl.getHandler().getStackInSlot(0);
		} else if (!tile.inputPearl.getHandler().getStackInSlot(0).isEmpty()) {
			itemStack = tile.inputPearl.getHandler().getStackInSlot(0);
		} else itemStack = ItemStack.EMPTY;

		if (!itemStack.isEmpty()) {
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
