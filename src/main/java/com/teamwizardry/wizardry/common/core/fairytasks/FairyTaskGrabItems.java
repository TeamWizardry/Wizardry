package com.teamwizardry.wizardry.common.core.fairytasks;

import com.teamwizardry.wizardry.api.StateGraph;
import com.teamwizardry.wizardry.api.entity.fairy.fairytasks.FairyTask;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.List;

public class FairyTaskGrabItems extends FairyTask {

	private final StateGraph<EntityFairy> graph = new StateGraph.Builder<EntityFairy>()
			.runWhile(fairy -> true, entityFairyBuilder -> entityFairyBuilder
					.run(fairy -> {
						if (fairy.isMoving()) return false;

						EntityItem farthestItem = getFarthestStack(fairy);
						if (farthestItem == null || farthestItem.getDistanceSq(fairy) < 1) return false;

						if (isPriorityTaken(fairy)) {
							fairy.setDataHeldItem(farthestItem.getItem());
							fairy.world.removeEntity(farthestItem);
						} else fairy.moveTo(farthestItem.getPosition());

						return true;
					})


					.run(fairy -> {
						if (fairy.isMoving()) return false;
						if (fairy.originPos == null) return false;

						if (isPriorityTaken(fairy)) return true;

						EntityItem closestItem = getClosestStack(fairy);
						if (closestItem == null) return false;

						fairy.setDataHeldItem(closestItem.getItem());
						fairy.world.removeEntity(closestItem);
						fairy.moveTo(fairy.originPos);
						return true;

					})

					.run(fairy -> {
						if (fairy.isMoving()) return false;
						if (isPriorityTaken(fairy)) return true;

						popItemFromHand(fairy);
						return true;
					})

					.wait(5))

			.build();

	private static List<EntityItem> getEntityItems(EntityFairy fairy) {
		return fairy.world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(fairy.getPosition()).grow(3), input -> input != null && !input.cannotPickup());
	}

	@Nullable
	private static EntityItem getClosestStack(EntityFairy fairy) {
		List<EntityItem> list = getEntityItems(fairy);
		list.sort((o1, o2) -> o1.getDistanceSq(fairy) < o2.getDistanceSq(fairy) ? -1 : 1);

		if (list.isEmpty()) return null;
		else return list.get(0);
	}

	@Nullable
	private static EntityItem getFarthestStack(EntityFairy fairy) {
		List<EntityItem> list = getEntityItems(fairy);
		list.sort((o1, o2) -> o1.getDistanceSq(fairy) < o2.getDistanceSq(fairy) ? 1 : -1);

		if (list.isEmpty()) return null;
		else return list.get(0);
	}

	private static void popItemFromHand(EntityFairy fairy) {
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
	public int getPriority() {
		return 0;
	}

	@Override
	public void onStart(EntityFairy fairy) {

	}

	@Override
	public void onTick(EntityFairy fairy) {
		graph.offer(fairy);
	}

	@Override
	public void onForceTrigger(EntityFairy fairy) {

		popItemFromHand(fairy);
	}

	@Override
	public void onEnd(EntityFairy fairy) {

	}

	@Override
	public void onConfigure(EntityFairy fairy, @Nullable BlockPos targetBlock, @Nullable Entity targetEntity, Vec3d lookVec) {

	}
}
