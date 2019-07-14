package com.teamwizardry.wizardry.common.core.fairytasks;

import com.teamwizardry.wizardry.api.entity.fairy.fairytasks.FairySequence;
import com.teamwizardry.wizardry.api.entity.fairy.fairytasks.FairySequenceBuilder;
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

	private static FairySequence sequence = new FairySequenceBuilder()
			.run(fairy -> {
				if (fairy.isMoving()) return false;

				boolean isPriorityTaken = isPriorityTaken(fairy);

				for (EntityItem entityItem : fairy.world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(fairy.getPosition()).grow(5))) {
					if (entityItem == null || entityItem.cannotPickup()) continue;

					if (isPriorityTaken) {
						fairy.setDataHeldItem(entityItem.getItem());
					} else fairy.moveTo(entityItem.getPositionVector());

					return true;
				}
				return false;
			})
			.waitIf(fairy -> fairy.isMoving() || !isPriorityTaken(fairy), 5)
			.run(fairy -> {
				if (fairy.isMoving()) return false;
				if (fairy.originPos == null) return true;

				boolean isPriorityTaken = isPriorityTaken(fairy);

				if (!isPriorityTaken) {
					for (EntityItem entityItem : fairy.world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(fairy.getPosition()).grow(5))) {
						if (entityItem == null || entityItem.cannotPickup()) continue;

						fairy.setDataHeldItem(entityItem.getItem());
						fairy.world.removeEntity(entityItem);
						fairy.moveTo(fairy.originPos);
						return true;
					}
				} else return true;

				return false;
			})
			.run(fairy -> {
				if (fairy.isMoving()) return false;

				ItemStack heldItem = fairy.getDataHeldItem();
				if (heldItem.isEmpty()) return true;

				EntityItem entityItem = new EntityItem(fairy.world, fairy.posX, fairy.posY, fairy.posZ, heldItem.copy());
				entityItem.motionX = 0;
				entityItem.motionY = 0;
				entityItem.motionZ = 0;
				entityItem.setPickupDelay(50);
				if (fairy.world.spawnEntity(entityItem)) {
					fairy.setDataHeldItem(ItemStack.EMPTY);
				}

				return true;
			})
			.build();

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public void onStart(EntityFairy fairy) {

	}

	@Override
	public void onTick(EntityFairy fairy) {
		sequence.tick(fairy);
	}

	@Override
	public void onForceTrigger(EntityFairy fairy) {

		ItemStack heldItem = fairy.getDataHeldItem();
		if (heldItem.isEmpty()) return;

		EntityItem entityItem = new EntityItem(fairy.world, fairy.posX, fairy.posY, fairy.posZ, heldItem.copy());
		entityItem.motionX = 0;
		entityItem.motionY = 0;
		entityItem.motionZ = 0;
		entityItem.setPickupDelay(50);
		if (fairy.world.spawnEntity(entityItem)) {
			fairy.setDataHeldItem(ItemStack.EMPTY);
		}
	}

	@Override
	public void onEnd(EntityFairy fairy) {

	}

	@Override
	public void onConfigure(EntityFairy fairy, @Nullable BlockPos targetBlock, @Nullable Entity targetEntity, Vec3d lookVec) {

	}
}
