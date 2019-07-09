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

		switch (phase) {
			case 1: {
				break;
			}
			case 2: {
				break;
			}
			case 3: {
				break;
			}
			case 4: {
				break;
			}
			case 5: {
				break;
			}
		}


		if (!heldItem.isEmpty()) {
			if (fairy.getPositionVector().subtract(new Vec3d(fairy.getDataOrigin())).lengthSquared() < 1) {

				EntityItem entityItem = new EntityItem(fairy.world, fairy.posX, fairy.posY, fairy.posZ, heldItem.copy());
				entityItem.setPickupDelay(50);
				if (fairy.world.spawnEntity(entityItem)) {
					fairy.setDataHeldItem(ItemStack.EMPTY);
				}
			}
		} else
			for (EntityItem entityItem : fairy.world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(fairy.getPosition()).grow(4))) {
				if (entityItem == null || entityItem.cannotPickup()) continue;

				if (fairy.getPositionVector().distanceTo(entityItem.getPositionVector()) < 1) {
					fairy.setDataHeldItem(entityItem.getItem().copy());
					entityItem.getItem().setCount(0);
					fairy.world.removeEntity(entityItem);
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
