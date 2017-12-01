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

import javax.swing.text.Position;

/**
 * Created by Saad on 6/11/2016.
 */
public class TileCraftingPlateRenderer extends TileRenderHandler<TileCraftingPlate> {

	private static Animator animator = new Animator();

	private PositionTracker[] trackers;

	private CachedStructure cachedStructure;
	private RandUtilSeed rand;

	public TileCraftingPlateRenderer(@NotNull TileCraftingPlate tile) {
		super(tile);

		animator.setUseWorldTicks(true);
 		trackers = new PositionTracker[tile.realInventory.getHandler().getSlots()];
		cachedStructure = new CachedStructure(((IStructure) tile.getBlockType()).getStructure().loc, tile.getWorld());
		rand = new RandUtilSeed(RandUtil.nextLong(100, 100000));

		// initialize every tracker
		for(int i = 0; i < trackers.length; i++) {
			trackers[i] = new PositionTracker(animator);
		}
	}
//
//	/**
//	 * Animates the position at `i` from (0, 0) to a random position, and sets the callback so at the end it will run
//	 * `addAnimation`
//	 *
//	 * This also removes the current animations for that position so there aren't conflicting animations.
//	 *
//	 * This method is called in the tile when a new item is added so the item flies out of the block
//	 */
//	public void spawnItem(int i) {
//		animator.removeAnimationsFor(positions[i]);
//		BasicAnimation<Vec3d[]> anim = new BasicAnimation<>(positions, "[" + i + "]");
//		anim.setFrom(Vec3d.ZERO);
//		anim.setTo(new Vec3d(RandUtil.nextDouble(-8, 8), RandUtil.nextDouble(2, 10), RandUtil.nextDouble(-8, 8)));
//		anim.setDuration(20);
//		anim.setCompletion(() -> {
//			addAnimation(i);
//		});
//		animator.add(anim);
//	}
//
//	/**
//	 * Animates from the current `i` position to a new random position and sets the callback so at the end it will do
//	 * this again
//	 */
//	private void addAnimation(int i) {
//
//		//
//		// constants involved in getting the new position to animate
//		//
//
//		double stepTime = 1; // the time for each step of the animation in seconds
//		double rotationSpeed = 15; // the speed in degrees per second
//
//		double rotationRandom = 5;
//		double radiusRandom = 0.5;
//		double yRandom = 0.25;
//
//		double minRadius = 3; // the largest radius to allow on the XZ plane
//		double maxRadius = 8; // the largest radius to allow on the XZ plane
//		double minY = 2; // the minimum Y before item positions are clamped
//		double maxY = 10; // the minimum Y before item positions are clamped
//
//		if((i % 2) == 0) rotationSpeed *= -1;
//
//		//
//		// actually calculating the position
//		//
//
//		// (simplified overview:
//		//    store Y position and randomly offset it
//		//    flatten pos onto XZ plane
//		//    adjust pos's radius randomly
//		//    re-add Y position into pos
//		//    rotate vector around Y axis
//		// )
//
//        Vec3d pos = positions[i];
//
//        if(pos.x == 0 && pos.z == 0) {
//            pos = new Vec3d(RandUtil.nextDouble(-8, 8), RandUtil.nextDouble(2, 10), RandUtil.nextDouble(-8, 8));
//		}
//
//        // adjust Y position randomly
//        double newY = pos.y + RandUtil.nextDouble(-yRandom, yRandom);
//        if(newY < minY) newY = minY;
//        if(newY > maxY) newY = maxY;
//
//        // flatten pos onto XZ plane
//        pos = new Vec3d(pos.x, 0, pos.z);
//
//        // adjust pos's radius randomly
//        double radius = pos.lengthVector();
//        radius += RandUtil.nextDouble(-radiusRandom, radiusRandom);
//        pos = pos.normalize().scale(radius);
//
//        // re-add Y position into pos
//		pos = new Vec3d(pos.x, newY, pos.z);
//
//		// rotate vector around Y axis
//        double rotationAmount = rotationSpeed * stepTime + RandUtil.nextDouble(-rotationRandom, rotationRandom);
//        pos = pos.rotateYaw((float)Math.toRadians(rotationAmount));
//
//
//        //
//		// Create the animation object
//		//
//
//		BasicAnimation<Vec3d[]> anim = new BasicAnimation<>(positions, "[" + i + "]");
//		anim.setTo(pos);
//		anim.setDuration(20);
//		final int finalI = i;
//		anim.setCompletion(() -> {
//			addAnimation(finalI);
//		});
//		animator.add(anim);
//	}

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
			PositionTracker tracker = trackers[i];
			if(!stack.isEmpty()) {
				float rotOffset = (i * 1234.56789f) % 360; // semi-randomly offset each item's rotation
				float itemRotation = (tile.getWorld().getTotalWorldTime() + rotOffset) + ClientTickHandler.getPartialTicks();
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.5, 0.5, 0.5);
				GlStateManager.rotate((float)tracker.preRotation, (float)tracker.preRotationAxis.x, (float)tracker.preRotationAxis.y, (float)tracker.preRotationAxis.z);
				double sphereRadius = 5;
				double height = MathHelper.sqrt(sphereRadius * sphereRadius - tracker.radius * tracker.radius);
				GlStateManager.translate(0, height, 0);
				GlStateManager.rotate((float)tracker.rotation, 0, 1, 0);
				GlStateManager.translate(tracker.radius, 0, 0);
				GlStateManager.scale(0.3, 0.3, 0.3);
				GlStateManager.rotate(itemRotation, 0, 1, 0);
				Minecraft.getMinecraft().getRenderItem().renderItem(stack, TransformType.NONE);
				GlStateManager.popMatrix();


//				if (!manager.isManaEmpty()) {
//					if (tile.inputPearl.getHandler().getStackInSlot(0).isEmpty() && RandUtil.nextInt(count > 0 && count / 2 > 0 ? count / 2 : 1) == 0)
//						LibParticles.CLUSTER_DRAPE(tile.getWorld(), new Vec3d(tile.getPos()).addVector(0.5, 0.5, 0.5).add(pos));
//
//					if (!tile.inputPearl.getHandler().getStackInSlot(0).isEmpty()) {
//						if (RandUtil.nextInt(count > 0 && count / 4 > 0 ? count / 4 : 1) == 0) {
//							LibParticles.CRAFTING_ALTAR_CLUSTER_SUCTION(tile.getWorld(), new Vec3d(tile.getPos()).addVector(0.5, 0.75, 0.5), new InterpBezier3D(pos, new Vec3d(0, 0, 0)));
//						}
//					}
//				}
			}
		}

//
//		for (int i = 0; i < tile.realInventory.getHandler().getSlots(); i++) {
//			if (!tile.realInventory.getHandler().getStackInSlot(i).isEmpty()) {
//				ItemStack stack = tile.realInventory.getHandler().getStackInSlot(i);
//
//				ClusterObject cluster = tile.renders[i];
//				if (cluster == null) continue;
//
//				cluster.tick(tile.getWorld(), tile.random);
//
//				double timeDifference = (tile.getWorld().getTotalWorldTime() - cluster.worldTime + partialTicks) / cluster.destTime;
//				Vec3d current = cluster.origin.add(cluster.dest.subtract(cluster.origin).scale(MathHelper.sin((float) (timeDifference * Math.PI / 2))));
//
//				if (!manager.isManaEmpty()) {
//					if (tile.inputPearl.getHandler().getStackInSlot(0).isEmpty() && RandUtil.nextInt(count > 0 && count / 2 > 0 ? count / 2 : 1) == 0)
//						LibParticles.CLUSTER_DRAPE(tile.getWorld(), new Vec3d(tile.getPos()).addVector(0.5, 0.5, 0.5).add(current));
//
//					if (!tile.inputPearl.getHandler().getStackInSlot(0).isEmpty()) {
//						if (RandUtil.nextInt(count > 0 && count / 4 > 0 ? count / 4 : 1) == 0) {
//							LibParticles.CRAFTING_ALTAR_CLUSTER_SUCTION(tile.getWorld(), new Vec3d(tile.getPos()).addVector(0.5, 0.75, 0.5), new InterpBezier3D(current, new Vec3d(0, 0, 0)));
//						}
//					}
//				}
//
//
//				GlStateManager.pushMatrix();
//				GlStateManager.translate(0.5 + current.x, 0.5 + current.y, 0.5 + current.z);
//				GlStateManager.scale(0.3, 0.3, 0.3);
//				GlStateManager.rotate((tile.getWorld().getTotalWorldTime()) + ClientTickHandler.getPartialTicks(), 0, 1, 0);
//				Minecraft.getMinecraft().getRenderItem().renderItem(stack, TransformType.NONE);
//				GlStateManager.popMatrix();
//			}
//		}



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

	public static class PositionTracker {
		public double rotation = RandUtil.nextDouble(0, 360);
		public double radius = RandUtil.nextDouble(0.5, 5);
		public Vec3d preRotationAxis = generateRotationAxis();
		public double preRotation = RandUtil.nextDouble(-45, 45);

		public PositionTracker(Animator animator) {
		    BasicAnimation<PositionTracker> rotationAnim = new BasicAnimation<>(this, "rotation");
		    if(RandUtil.nextBoolean())
				rotationAnim.setTo(rotation - 360);
		    else
		    	rotationAnim.setTo(rotation + 360);
		    rotationAnim.setRepeatCount(-1);
		    rotationAnim.setDuration(RandUtil.nextFloat(120,480));

			BasicAnimation<PositionTracker> radiusAnim = new BasicAnimation<>(this, "radius");
			radiusAnim.setFrom(radius - RandUtil.nextDouble(0, 1.5));
			radiusAnim.setTo(radius + RandUtil.nextDouble(0, 1.5));
			radiusAnim.setEasing(Easing.easeInOutSine);
			radiusAnim.setRepeatCount(-1);
			radiusAnim.setShouldReverse(true);
			radiusAnim.setDuration(RandUtil.nextFloat(120,480));
			radiusAnim.setStart(RandUtil.nextFloat(0, 40));

			BasicAnimation<PositionTracker> axisAnim = new BasicAnimation<>(this, "preRotationAxis");
			axisAnim.setTo(generateRotationAxis());
			axisAnim.setEasing(Easing.easeInOutSine);
			axisAnim.setRepeatCount(-1);
			axisAnim.setShouldReverse(true);
			axisAnim.setDuration(RandUtil.nextFloat(120,480));

			animator.add(rotationAnim, radiusAnim, axisAnim);
		}
		private Vec3d generateRotationAxis() {
			return new Vec3d(RandUtil.nextDouble(-1, 1), 0, RandUtil.nextDouble(-1, 1)).normalize();
		}
	}
}
