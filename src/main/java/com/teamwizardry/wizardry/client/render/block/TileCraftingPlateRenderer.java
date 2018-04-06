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
import com.teamwizardry.wizardry.client.core.StructureErrorRenderer;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.HashSet;

/**
 * Created by Demoniaque on 6/11/2016.
 */
@SideOnly(Side.CLIENT)
public class TileCraftingPlateRenderer extends TileRenderHandler<TileCraftingPlate> {

	private static Animator animator = new Animator();

	private LocationAndAngle[] locationsAndAngles;

	private int index = 0;

	private CachedStructure cachedStructure;

	public TileCraftingPlateRenderer(@Nonnull TileCraftingPlate tile) {
		super(tile);

		animator.setUseWorldTicks(true);
		cachedStructure = new CachedStructure(((IStructure) tile.getBlockType()).getStructure().loc, tile.getWorld());
		locationsAndAngles = new LocationAndAngle[tile.realInventory.getHandler().getSlots()];

		for (int i = 0; i < tile.realInventory.getHandler().getSlots(); i++) {
			if (!tile.realInventory.getHandler().getStackInSlot(i).isEmpty()) {
				addAnimation();
			}
		}
	}

	private int push(double x, double y, double z, double angle) {
		locationsAndAngles[index++] = new LocationAndAngle(x, y, z, angle);
		return index - 1;
	}

	private LocationAndAngle popLast() {
		return locationsAndAngles[index - 1];
	}

	private LocationAndAngle pop(int index) {
		return locationsAndAngles[index];
	}

	public void animationLoop(int i, boolean motionInvert) {
		LocationAndAngle locationAndAngle = pop(i);
		if (locationAndAngle == null) return;

		CapManager manager = new CapManager(tile.getWizardryCap());

		Vec3d newDest;
		double t = manager.getCap() == null ? 1 : 1 - (manager.getMana() / manager.getMaxMana());

		double radius = RandUtil.nextDouble(5, 8) * t;
		locationAndAngle.angle += RandUtil.nextDouble(-1.5, 1.5);
		double x = MathHelper.cos((float) locationAndAngle.angle) * radius;
		double z = MathHelper.sin((float) locationAndAngle.angle) * radius;

		newDest = new Vec3d(x, (2 + (RandUtil.nextFloat() * 7)) * t, z);

		BasicAnimation<LocationAndAngle> anim = new BasicAnimation<>(locationAndAngle, "location");
		anim.setTo(newDest);
		anim.setDuration((float) (RandUtil.nextDouble(50, 100) * (tile.suckingCooldown <= 0 ? MathHelper.clamp(t * 2, 0, 1) : t)));
		anim.setEasing(!manager.isManaEmpty() ? Easing.easeInOutQuint : Easing.easeInOutSine);
		anim.setCompletion(() -> animationLoop(i, !motionInvert));
		animator.add(anim);
	}

	public void addAnimation() {
		int index = push(0, 0.5, 0, RandUtil.nextDouble(0, 360));

		animationLoop(index, false);
	}

	public void removeLast() {
		locationsAndAngles[index - 1] = null;
	}

	public void clearAll() {
		locationsAndAngles = new LocationAndAngle[tile.realInventory.getHandler().getSlots()];
	}

	@Override
	public void render(float partialTicks, int destroyStage, float alpha) {
		HashSet<BlockPos> errors = ((IStructure) tile.getBlockType()).getErroredBlocks(tile.getWorld(), tile.getPos());
		if (tile.revealStructure && tile.getBlockType() instanceof IStructure && !errors.isEmpty()) {

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

		} else if (!tile.revealStructure && !errors.isEmpty()) {
			for (BlockPos error : errors)
				StructureErrorRenderer.INSTANCE.addError(error);
		}

		// render each item at its current position
		for (int i = 0; i < index; i++) {

			ItemStack stack = tile.realInventory.getHandler().getStackInSlot(i);
			LocationAndAngle locationsAndAngle = locationsAndAngles[i];

			if (!stack.isEmpty() && locationsAndAngle != null) {

				{
					GlStateManager.pushMatrix();
					GlStateManager.translate(0.5 + locationsAndAngle.location.x, 1 + locationsAndAngle.location.y, 0.5 + locationsAndAngle.location.z);
					GlStateManager.scale(0.3, 0.3, 0.3);
					GlStateManager.rotate((locationsAndAngle.randX + tile.getWorld().getTotalWorldTime()) + ClientTickHandler.getPartialTicks(), 0, 1, 0);
					GlStateManager.rotate((locationsAndAngle.randY + tile.getWorld().getTotalWorldTime()) + ClientTickHandler.getPartialTicks(), 1, 0, 0);
					GlStateManager.rotate((locationsAndAngle.randZ + tile.getWorld().getTotalWorldTime()) + ClientTickHandler.getPartialTicks(), 0, 0, 1);

					GlStateManager.enableLighting();
					GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
					RenderHelper.disableStandardItemLighting();
					Minecraft.getMinecraft().getRenderItem().renderItem(stack, TransformType.NONE);
					RenderHelper.enableStandardItemLighting();
					GlStateManager.popMatrix();
				}

				if (tile.suckingCooldown <= 0) {
					if (RandUtil.nextInt(index / 2) == 0) {
						LibParticles.CLUSTER_DRAPE(
								tile.getWorld(),
								locationsAndAngle.location.add(new Vec3d(tile.getPos())).addVector(0.5, 0.5, 0.5));
					}
				} else {
					if (RandUtil.nextInt(index / 4) == 0) {
						LibParticles.CRAFTING_ALTAR_CLUSTER_SUCTION(
								tile.getWorld(),
								new Vec3d(tile.getPos()).addVector(0.5, 0.75, 0.5),
								new InterpBezier3D(locationsAndAngle.location, new Vec3d(0, 0, 0)));
					}
				}
			}
		}

		ItemStack pearlToRender = tile.getInputPearl();
		if (pearlToRender.isEmpty()) {
			pearlToRender = tile.getOutputPearl();
			if (pearlToRender.isEmpty()) {
				pearlToRender = ItemStack.EMPTY;
			}
		}

		if (!pearlToRender.isEmpty()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.5, 1, 0.5);
			GlStateManager.scale(0.4, 0.4, 0.4);
			GlStateManager.rotate((float) (tile.getWorld().getTotalWorldTime() * 10.0), 0, 1, 0);
			GlStateManager.translate(0, 0.5 + Math.sin((tile.getWorld().getTotalWorldTime() + partialTicks) / 5) / 10.0, 0);
			Minecraft.getMinecraft().getRenderItem().renderItem(pearlToRender, TransformType.NONE);
			GlStateManager.popMatrix();
		} else if (errors.isEmpty() && RandUtil.nextInt(4) == 0) {
			LibParticles.CRAFTING_ALTAR_IDLE(tile.getWorld(), new Vec3d(tile.getPos()).addVector(0.5, 0.7, 0.5));
		}
	}

	public static class LocationAndAngle {

		public double angle;
		public Vec3d location;
		// for randomized item rotation
		public int randX, randY, randZ;

		LocationAndAngle(double x, double y, double z, double angle) {
			location = new Vec3d(x, y, z);
			this.angle = angle;
			randX = RandUtil.nextInt(-1000, 1000);
			randY = RandUtil.nextInt(-1000, 1000);
			randZ = RandUtil.nextInt(-1000, 1000);
		}
	}
}
