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
	public int getPriority() {
		return 0;
	}

	@Override
	public void onStart(EntityFairy fairy) {

	}

	@Override
	public void onTick(EntityFairy fairy) {
		if (fairy.isMoving()) return;

		ItemStack heldItem = fairy.getDataHeldItem();

		boolean isPriorityTaken = doesAttachedFairyTakePriority(fairy);

		if (!heldItem.isEmpty()) {
			if (fairy.getPositionVector().subtract(new Vec3d(fairy.getDataOriginBlock())).lengthSquared() < 1) {

				EntityItem entityItem = new EntityItem(fairy.world, fairy.posX, fairy.posY, fairy.posZ, heldItem.copy());
				entityItem.motionX = 0;
				entityItem.motionY = 0;
				entityItem.motionZ = 0;
				entityItem.setPickupDelay(50);
				if (fairy.world.spawnEntity(entityItem)) {
					fairy.setDataHeldItem(ItemStack.EMPTY);
				}
			} else if (isPriorityTaken) {
				fairy.moveTo(fairy.getDataOriginBlock());
			}
		} else {
			for (EntityItem entityItem : fairy.world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(fairy.getPosition()).grow(5))) {
				if (entityItem == null || entityItem.cannotPickup()) continue;

				if (isPriorityTaken) {
					fairy.setDataHeldItem(entityItem.getItem());

				} else {
					double distFairyToItem = fairy.getPositionVector().distanceTo(entityItem.getPositionVector());
					double distItemToHome = new Vec3d(fairy.getDataOriginBlock()).add(0.5, 0.5, 0.5).distanceTo(entityItem.getPositionVector());
					if (distItemToHome > 0.5 && distFairyToItem <= 0.5) {
						fairy.setDataHeldItem(entityItem.getItem());
						fairy.world.removeEntity(entityItem);
						fairy.moveTo(fairy.getDataOriginBlock());
					} else {
						fairy.moveTo(entityItem.getPositionVector());
					}
				}
				return;
			}
		}
	}

	@Override
	public void onEnd(EntityFairy fairy) {

	}

	@Override
	public void onConfigure(EntityFairy fairy, @Nullable BlockPos targetBlock, @Nullable Entity targetEntity, Vec3d lookVec) {

	}
}
