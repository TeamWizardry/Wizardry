package com.teamwizardry.wizardry.api.entity.fairy.fairytasks;

import com.teamwizardry.wizardry.common.entity.EntityFairy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FairyTaskGrabItems extends FairyTask {
	@NotNull
	@Override
	public String getNBTKey() {
		return "grab_items";
	}

	@Nonnull
	@Override
	public ItemStack getFoodItem() {
		return new ItemStack(Items.APPLE);
	}

	@Override
	public void onTrigger(EntityFairy fairy) {
		if (fairy.isMoving()) return;

		ItemStack heldItem = fairy.getDataHeldItem();
		if (!heldItem.isEmpty()) {
			if (fairy.getPositionVector().subtract(new Vec3d(fairy.getHomePosition())).lengthSquared() < 1) {

				EntityItem entityItem = new EntityItem(fairy.world, fairy.posX, fairy.posY, fairy.posZ, heldItem);
				if (fairy.world.spawnEntity(entityItem)) {
					fairy.setDataHeldItem(ItemStack.EMPTY);
				}
			}
		}

		if (heldItem.isEmpty())
			for (EntityItem entityItem : fairy.world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(fairy.getPosition()).grow(3))) {
				if (entityItem == null) continue;

				if (fairy.getPositionVector().subtract(entityItem.getPositionVector()).lengthSquared() < 1) {
					fairy.setDataHeldItem(entityItem.getItem());
					entityItem.getItem().setCount(0);
					fairy.moveTo(fairy.getDataOrigin());
				} else {
					fairy.moveTo(entityItem.getPosition());
				}

				return;
			}
	}

	@Override
	public void onConfigure(EntityFairy fairy, @Nullable BlockPos targetBlock, @Nullable Entity targetEntity, Vec3d lookVec) {

	}

	@Override
	public boolean shouldTrigger(EntityFairy fairy) {
		for (EntityItem entityItem : fairy.world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(fairy.getPosition()).grow(3))) {
			if (entityItem == null) continue;

			return true;
		}

		return !fairy.getDataHeldItem().isEmpty();
	}
}
