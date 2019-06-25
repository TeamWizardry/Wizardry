package com.teamwizardry.wizardry.client.render.block;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.animator.Animator;
import com.teamwizardry.librarianlib.features.animator.Easing;
import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpBezier3D;
import com.teamwizardry.librarianlib.features.tesr.TileRenderHandler;
import com.teamwizardry.wizardry.api.block.ICraftingPlateRecipe;
import com.teamwizardry.wizardry.api.block.IStructure;
import com.teamwizardry.wizardry.api.capability.player.mana.IManaCapability;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.client.core.renderer.StructureErrorRenderer;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
import com.teamwizardry.wizardry.crafting.CraftingPlateRecipeManager;
import com.teamwizardry.wizardry.init.ModStructures;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * Created by Demoniaque on 6/11/2016.
 */
@SideOnly(Side.CLIENT)
public class TileCraftingPlateRenderer extends TileRenderHandler<TileCraftingPlate> {

	private static Animator animator = new Animator();

	private IntObjectMap<HoveringStack> hoveringStacks;

	public TileCraftingPlateRenderer(@Nonnull TileCraftingPlate tile) {
		super(tile);

		animator.setUseWorldTicks(true);
		hoveringStacks = new IntObjectHashMap<>(tile.realInventory.getHandler().getSlots());

		for (int i = 0; i < tile.realInventory.getHandler().getSlots(); i++) {
			update(i, tile.realInventory.getHandler().getStackInSlot(i));
		}
	}

	public void update(int slot, ItemStack stack) {
		if (stack.isEmpty()) {
			hoveringStacks.remove(slot);
		} else {
			hoveringStacks.put(slot, new HoveringStack(stack, tile, 0, 0.5, 0, RandUtil.nextDouble(0, 360)));
		}
	}

	@Override
	public void render(float partialTicks, int destroyStage, float alpha) {
		ArrayList<BlockPos> errors = new ArrayList<>(((IStructure) tile.getBlockType()).testStructure(tile.getWorld(), tile.getPos()));
		errors.sort(Vec3i::compareTo);

		ItemStack input = tile.getInput();
		if (input.isEmpty()) {
			input = tile.getOutput();
			if (input.isEmpty()) {
				input = ItemStack.EMPTY;
			}
		}

		ICraftingPlateRecipe recipeForItem = CraftingPlateRecipeManager.getRecipe(tile.getWorld(), tile.getPos(), input);
		if (recipeForItem != null) recipeForItem.renderInput(tile.getWorld(), tile.getPos(), input, partialTicks);

		if (!errors.isEmpty() && tile.revealStructure && tile.getBlockType() instanceof IStructure) {
			ModStructures.structureManager.draw(ModStructures.CRAFTING_PLATE, (float) (Math.sin(ClientTickHandler.getTicks() / 10.0) + 1) / 10.0f + 0.3f);
		}

		if (!errors.isEmpty()) {
			for (BlockPos error : errors) {
				StructureErrorRenderer.addError(error);
			}
			return;
		} else if (tile.getWorld().isAirBlock(tile.getPos().offset(EnumFacing.UP)) && !CraftingPlateRecipeManager.doesRecipeExist(tile.getWorld(), tile.getPos(), input) && RandUtil.nextInt(4) == 0) {
			LibParticles.CRAFTING_ALTAR_IDLE(tile.getWorld(), new Vec3d(tile.getPos()).add(0.5, 0.7, 0.5));
		}

		// render each item at its current position
		final int mapSize = hoveringStacks.size();
		for (HoveringStack hoveringStack : hoveringStacks.values()) {

			if (!hoveringStack.stack.isEmpty()) {

				{
					GlStateManager.pushMatrix();
					GlStateManager.translate(0.5 + hoveringStack.location.x, 1 + hoveringStack.location.y, 0.5 + hoveringStack.location.z);
					GlStateManager.scale(0.3, 0.3, 0.3);
					GlStateManager.rotate((hoveringStack.randX + ClientTickHandler.getTicks()), 0, 1, 0);
					GlStateManager.rotate((hoveringStack.randY + ClientTickHandler.getTicks()), 1, 0, 0);
					GlStateManager.rotate((hoveringStack.randZ + ClientTickHandler.getTicks()), 0, 0, 1);

					GlStateManager.enableLighting();
					RenderHelper.disableStandardItemLighting();
					Minecraft.getMinecraft().getRenderItem().renderItem(hoveringStack.stack, TransformType.NONE);
					RenderHelper.enableStandardItemLighting();
					GlStateManager.popMatrix();
				}

				if (tile.suckingCooldown <= 0) {
					if (RandUtil.nextInt(mapSize / 2) == 0) {
						LibParticles.CLUSTER_DRAPE(
								tile.getWorld(),
								hoveringStack.location.add(new Vec3d(tile.getPos())).add(0.5, 0.5, 0.5));
					}
				} else {
					if (RandUtil.nextInt(mapSize) == 0) {
						LibParticles.CRAFTING_ALTAR_CLUSTER_SUCTION(
								tile.getWorld(),
								new Vec3d(tile.getPos()).add(0.5, 0.75, 0.5),
								new InterpBezier3D(hoveringStack.location, new Vec3d(0, 0, 0)));
					}
				}
			}
		}
	}

	public static class HoveringStack {

		public double angle;
		public Vec3d location;
		// for randomized item rotation
		private int randX, randY, randZ;
		public final ItemStack stack;
		private final TileCraftingPlate tile;

		HoveringStack(ItemStack stack, TileCraftingPlate tile, double x, double y, double z, double angle) {
			this.stack = stack;
			this.tile = tile;
			location = new Vec3d(x, y, z);
			this.angle = angle;
			randX = RandUtil.nextInt(-1000, 1000);
			randY = RandUtil.nextInt(-1000, 1000);
			randZ = RandUtil.nextInt(-1000, 1000);

			animationLoop();
		}


		private void animationLoop() {
			if (tile.isInvalid()) return;

			IManaCapability cap = tile.getWizardryCap();

			Vec3d newDest;
			double t = cap == null ? 1 : 1 - (cap.getMana() / cap.getMaxMana());

			double radius = RandUtil.nextDouble(5, 8) * t;
			angle += RandUtil.nextDouble(-1.5, 1.5);
			double x = MathHelper.cos((float) angle) * radius;
			double z = MathHelper.sin((float) angle) * radius;

			newDest = new Vec3d(x, (2 + (RandUtil.nextFloat() * 7)) * t, z);

			new BasicAnimation<>(this, "location")
					.ease((cap != null && cap.getMana() != 0) ? Easing.easeInQuint : Easing.easeInOutSine)
					.to(newDest)
					.duration((float) (RandUtil.nextDouble(50, 100) * (tile.suckingCooldown <= 0 ? MathHelper.clamp(t * 2, 0, 1) : t)))
					.completion(this::animationLoop)
					.addTo(animator);
		}
	}
}
