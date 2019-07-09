package com.teamwizardry.wizardry.common.core.fairytasks;

import com.teamwizardry.wizardry.api.entity.fairy.fairytasks.FairyTask;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public class FairyTaskGrabItems extends FairyTask {

	@Override
	public void onTrigger(EntityFairy fairy) {
		if (fairy.isMoving()) return;

		ItemStack heldItem = fairy.getDataHeldItem();

		if (!heldItem.isEmpty()) {
			if (fairy.getPositionVector().subtract(new Vec3d(fairy.getDataOrigin())).lengthSquared() < 1) {

				EntityItem entityItem = new EntityItem(fairy.world, fairy.posX, fairy.posY, fairy.posZ, heldItem.copy());
				entityItem.motionX = 0;
				entityItem.motionY = 0;
				entityItem.motionZ = 0;
				entityItem.setPickupDelay(50);
				if (fairy.world.spawnEntity(entityItem)) {
					fairy.setDataHeldItem(ItemStack.EMPTY);
				}
			} else {
				fairy.moveTo(fairy.getDataOrigin());
			}
		} else
			for (EntityItem entityItem : fairy.world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(fairy.getPosition()).grow(5))) {
				if (entityItem == null || entityItem.cannotPickup()) continue;

				double distFairyToItem = fairy.getPositionVector().distanceTo(entityItem.getPositionVector());
				double distItemToHome = new Vec3d(fairy.getDataOrigin()).add(0.5, 0.5, 0.5).distanceTo(entityItem.getPositionVector());
				if (distItemToHome > 0.5 && distFairyToItem <= 0.5) {
					fairy.setDataHeldItem(entityItem.getItem().copy());
					entityItem.getItem().setCount(0);
					fairy.world.removeEntity(entityItem);
					fairy.moveTo(fairy.getDataOrigin());
				} else {
					fairy.moveTo(entityItem.getPositionVector());
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
