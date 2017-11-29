package com.teamwizardry.wizardry.client.render.block;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.animator.Animator;
import com.teamwizardry.librarianlib.features.animator.Easing;
import com.teamwizardry.librarianlib.features.animator.animations.Keyframe;
import com.teamwizardry.librarianlib.features.animator.animations.KeyframeAnimation;
import com.teamwizardry.librarianlib.features.animator.animations.ScheduledEventAnimation;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpBezier3D;
import com.teamwizardry.wizardry.api.block.CachedStructure;
import com.teamwizardry.wizardry.api.block.IStructure;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.render.CPItemRenderer;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

/**
 * Created by Saad on 6/11/2016.
 */
public class TileCraftingPlateRenderer extends TileEntitySpecialRenderer<TileCraftingPlate> {

	private static Animator animator = new Animator();

	@Override
	public void render(TileCraftingPlate te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (te.revealStructure && te.getBlockType() instanceof IStructure && !((IStructure) te.getBlockType()).isStructureComplete(te.getWorld(), te.getPos())) {

			IStructure structure = ((IStructure) te.getBlockType());

			GlStateManager.pushMatrix();
			GlStateManager.enableAlpha();
			GlStateManager.enableLighting();
			GlStateManager.enableBlend();
			GlStateManager.enableCull();
			GlStateManager.disableDepth();
			GlStateManager.enableRescaleNormal();
			GlStateManager.color(1, 1, 1);
			//GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

			GlStateManager.translate(x, y, z);
			GlStateManager.translate(-structure.offsetToCenter().getX(), -structure.offsetToCenter().getY(), -structure.offsetToCenter().getZ());
			Minecraft mc = Minecraft.getMinecraft();
			Tessellator tes = Tessellator.getInstance();
			BufferBuilder buffer = tes.getBuffer();

			CachedStructure cachedStructure = ((IStructure) te.getBlockType()).getStructure();

			mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

			for (BlockRenderLayer layer : cachedStructure.blocks.keySet()) {
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
				buffer.addVertexData(cachedStructure.vboCaches.get(layer));

				for (int i = 0; i < buffer.getVertexCount(); i++) {
					int idx = buffer.getColorIndex(i + 1);
					buffer.putColorRGBA(idx, 255, 255, 255, 250);
				}
				tes.draw();
			}

			GlStateManager.disableAlpha();
			GlStateManager.disableBlend();
			GlStateManager.disableCull();
			GlStateManager.enableDepth();
			GlStateManager.popMatrix();
			return;
		}

		ItemStack pearl = te.inputPearl.getHandler().getStackInSlot(0);
		CapManager manager = new CapManager(pearl);

		int count = 0;
		for (int i = 0; i < te.realInventory.getHandler().getSlots(); i++) {
			if (!te.realInventory.getHandler().getStackInSlot(i).isEmpty()) {
				count++;
			}
		}

		for (int i = 0; i < te.realInventory.getHandler().getSlots(); i++) {
			if (!te.realInventory.getHandler().getStackInSlot(i).isEmpty()) {
				ItemStack stack = te.realInventory.getHandler().getStackInSlot(i);

				if (te.renderer[i] == null) continue;

				if (te.renderer[i].reset) {
					Minecraft.getMinecraft().player.sendChatMessage(i + ": " + 1);
					te.renderer[i].reset = false;
					te.renderer[i].x = te.renderer[i].prevX;
					te.renderer[i].y = te.renderer[i].prevY;
					te.renderer[i].z = te.renderer[i].prevZ;
				} else {
					Minecraft.getMinecraft().player.sendChatMessage(i + ": " + 2);
					continue;
				}

				Vec3d newDest;
				if (manager.getCap() == null || !manager.isManaEmpty()) {
					Minecraft.getMinecraft().player.sendChatMessage(i + ": " + 3);

					double t = manager.getCap() == null ? 1 : manager.getMana() / manager.getMaxMana();

					double radius = RandUtil.nextDouble(5, 8) * t;

					double angle = RandUtil.nextDouble(-1.5, 1.5);
					double x1 = MathHelper.cos((float) angle) * radius;
					double z1 = MathHelper.sin((float) angle) * radius;

					newDest = new Vec3d(x1, (2 + (RandUtil.nextFloat() * 7)) * t, z1);
				} else {
					Minecraft.getMinecraft().player.sendChatMessage(i + ": " + 3.5);
					newDest = new Vec3d(RandUtil.nextDouble(-0.3, 0.3), RandUtil.nextDouble(0.5, 0.6), RandUtil.nextDouble(-0.3, 0.3));
				}

				KeyframeAnimation<CPItemRenderer> animX = new KeyframeAnimation<>(te.renderer[i], "x");
				animX.setDuration(200);
				animX.setKeyframes(new Keyframe[]{
						new Keyframe(0, te.renderer[i].x, Easing.linear),
						new Keyframe(1f, newDest.x, Easing.easeOutExpo),
				});
				KeyframeAnimation<CPItemRenderer> animY = new KeyframeAnimation<>(te.renderer[i], "y");
				animY.setDuration(200);
				animY.setKeyframes(new Keyframe[]{
						new Keyframe(0, te.renderer[i].y, Easing.linear),
						new Keyframe(1f, newDest.y, Easing.easeOutExpo),
				});
				KeyframeAnimation<CPItemRenderer> animZ = new KeyframeAnimation<>(te.renderer[i], "z");
				animZ.setDuration(200);
				animZ.setKeyframes(new Keyframe[]{
						new Keyframe(0, te.renderer[i].z, Easing.linear),
						new Keyframe(1f, newDest.x, Easing.easeOutExpo),
				});

				int finalI = i;
				ScheduledEventAnimation sched = new ScheduledEventAnimation(200, () -> {
					Minecraft.getMinecraft().player.sendChatMessage(finalI + ": " + 4);
					te.renderer[finalI].prevX = newDest.x;
					te.renderer[finalI].prevY = newDest.y;
					te.renderer[finalI].prevZ = newDest.z;
					te.renderer[finalI].reset = true;
				});

				animator.add(animX, animY, animZ, sched);

				if (te.renderer[i] == null) continue;

				if (!manager.isManaEmpty()) {
					Vec3d loc = new Vec3d(te.renderer[i].x, te.renderer[i].y, te.renderer[i].z);
					if (!te.isCrafting && RandUtil.nextInt(count > 0 && count / 2 > 0 ? count / 2 : 1) == 0)
						LibParticles.CLUSTER_DRAPE(te.getWorld(), new Vec3d(te.getPos()).addVector(0.5, 0.5, 0.5).add(loc));

					if (te.isCrafting && (te.output != null)) {
						if (RandUtil.nextInt(count > 0 && count / 4 > 0 ? count / 4 : 1) == 0) {
							LibParticles.CRAFTING_ALTAR_CLUSTER_SUCTION(te.getWorld(), new Vec3d(te.getPos()).addVector(0.5, 0.75, 0.5), new InterpBezier3D(loc, new Vec3d(0, 0, 0)));
						}
					}
				}

				GlStateManager.pushMatrix();
				GlStateManager.translate(x + 0.5 + te.renderer[i].x, y + 0.5 + te.renderer[i].y, z + 0.5 + te.renderer[i].z);
				GlStateManager.scale(0.3, 0.3, 0.3);
				GlStateManager.rotate((te.getWorld().getTotalWorldTime()) + ClientTickHandler.getPartialTicks(), 0, 1, 0);
				Minecraft.getMinecraft().getRenderItem().renderItem(stack, TransformType.NONE);
				GlStateManager.popMatrix();
			}
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
