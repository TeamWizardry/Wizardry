package com.teamwizardry.wizardry.client.render.block;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.animator.Animator;
import com.teamwizardry.librarianlib.features.animator.Easing;
import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpBezier3D;
import com.teamwizardry.librarianlib.features.tesr.TileRenderHandler;
import com.teamwizardry.wizardry.api.block.ICraftingPlateRecipe;
import com.teamwizardry.wizardry.api.block.IStructure;
import com.teamwizardry.wizardry.api.capability.player.mana.ManaManager;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.client.core.renderer.StructureErrorRenderer;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
import com.teamwizardry.wizardry.crafting.CraftingPlateRecipeManager;
import com.teamwizardry.wizardry.init.ModStructures;
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
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by Demoniaque on 6/11/2016.
 */
@SideOnly(Side.CLIENT)
public class TileCraftingPlateRenderer extends TileRenderHandler<TileCraftingPlate> {

	private static Animator animator = new Animator();

	private WeakHashMap<Integer, LocationAndAngle> locationsAndAngles;

	public TileCraftingPlateRenderer(@Nonnull TileCraftingPlate tile) {
		super(tile);

		animator.setUseWorldTicks(true);
		locationsAndAngles = new WeakHashMap<>();

		for (int i = 0; i < tile.realInventory.getHandler().getSlots(); i++) {
			update(i);
		}
	}

	public void update(int slot) {
		ItemStack stack = tile.realInventory.getHandler().getStackInSlot(slot);
		if (stack.isEmpty()) {
			locationsAndAngles.remove(slot);
		} else {
			locationsAndAngles.put(slot, new LocationAndAngle(tile, 0, 0.5, 0, RandUtil.nextDouble(0, 360)));
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
		final int mapSize = locationsAndAngles.size();
		for (Map.Entry<Integer, LocationAndAngle> entry : locationsAndAngles.entrySet()) {

			ItemStack stack = tile.realInventory.getHandler().getStackInSlot(entry.getKey());
			LocationAndAngle locationsAndAngle = entry.getValue();

			if (!stack.isEmpty() && locationsAndAngle != null) {

				{
					GlStateManager.pushMatrix();
					GlStateManager.translate(0.5 + locationsAndAngle.location.x, 1 + locationsAndAngle.location.y, 0.5 + locationsAndAngle.location.z);
					GlStateManager.scale(0.3, 0.3, 0.3);
					GlStateManager.rotate((locationsAndAngle.randX + ClientTickHandler.getTicks()), 0, 1, 0);
					GlStateManager.rotate((locationsAndAngle.randY + ClientTickHandler.getTicks()), 1, 0, 0);
					GlStateManager.rotate((locationsAndAngle.randZ + ClientTickHandler.getTicks()), 0, 0, 1);

					GlStateManager.enableLighting();
					RenderHelper.disableStandardItemLighting();
					Minecraft.getMinecraft().getRenderItem().renderItem(stack, TransformType.NONE);
					RenderHelper.enableStandardItemLighting();
					GlStateManager.popMatrix();
				}

				if (tile.suckingCooldown <= 0) {
					if (RandUtil.nextInt(mapSize / 2) == 0) {
						LibParticles.CLUSTER_DRAPE(
								tile.getWorld(),
								locationsAndAngle.location.add(new Vec3d(tile.getPos())).add(0.5, 0.5, 0.5));
					}
				} else {
					if (RandUtil.nextInt(mapSize / 4) == 0) {
						LibParticles.CRAFTING_ALTAR_CLUSTER_SUCTION(
								tile.getWorld(),
								new Vec3d(tile.getPos()).add(0.5, 0.75, 0.5),
								new InterpBezier3D(locationsAndAngle.location, new Vec3d(0, 0, 0)));
					}
				}
			}
		}
	}

	public static class LocationAndAngle {

		public double angle;
		public Vec3d location;
		// for randomized item rotation
		private int randX, randY, randZ;
		private TileCraftingPlate tile;

		LocationAndAngle(TileCraftingPlate tile, double x, double y, double z, double angle) {
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

			try (ManaManager.CapManagerBuilder manager = ManaManager.forObject(tile.getWizardryCap())) {

				Vec3d newDest;
				double t = tile.getWizardryCap() == null ? 1 : 1 - (manager.getMana() / manager.getMaxMana());

				double radius = RandUtil.nextDouble(5, 8) * t;
				angle += RandUtil.nextDouble(-1.5, 1.5);
				double x = MathHelper.cos((float) angle) * radius;
				double z = MathHelper.sin((float) angle) * radius;

				newDest = new Vec3d(x, (2 + (RandUtil.nextFloat() * 7)) * t, z);

				BasicAnimation<LocationAndAngle> anim = new BasicAnimation<>(this, "location");
				anim.setTo(newDest);
				anim.setDuration((float) (RandUtil.nextDouble(50, 100) * (tile.suckingCooldown <= 0 ? MathHelper.clamp(t * 2, 0, 1) : t)));
				anim.setEasing(!manager.isManaEmpty() ? Easing.easeInOutQuint : Easing.easeInOutSine);
				anim.setCompletion(this::animationLoop);
				animator.add(anim);
			}
		}
	}
}
