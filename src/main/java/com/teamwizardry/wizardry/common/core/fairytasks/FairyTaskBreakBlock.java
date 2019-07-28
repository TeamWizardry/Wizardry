package com.teamwizardry.wizardry.common.core.fairytasks;

import com.teamwizardry.wizardry.api.StateGraph;
import com.teamwizardry.wizardry.api.entity.fairy.fairytasks.FairyTask;
import com.teamwizardry.wizardry.api.util.BlockUtils;
import com.teamwizardry.wizardry.api.util.RayTrace;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public class FairyTaskBreakBlock extends FairyTask {

	private final StateGraph<EntityFairy> graph = new StateGraph.Builder<EntityFairy>()
			.runWhile(fairy -> true, entityFairyBuilder -> entityFairyBuilder
					.run(fairy -> {
						if (fairy.isMoving()) return false;

						return !breakBlock(fairy);
					})
					.wait(10))
			.build();

	private static boolean breakBlock(EntityFairy fairy) {
		Vec3d lookTarget = fairy.getLookTarget();
		if (lookTarget == null) return false;

		RayTraceResult trace = new RayTrace(fairy.world, lookTarget, fairy.getPositionVector(), 3)
				.setReturnLastUncollidableBlock(false)
				.setEntityFilter(input -> input != null && !input.equals(fairy))
				.setIgnoreBlocksWithoutBoundingBoxes(true)
				.trace();

		if (trace.typeOfHit == RayTraceResult.Type.BLOCK) {
			EntityPlayerMP player = BlockUtils.makeBreaker(fairy.world, fairy.getPosition(), null);
			//	player.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.IRON_PICKAXE));
			return BlockUtils.breakBlock(fairy.world, trace.getBlockPos(), fairy.world.getBlockState(trace.getBlockPos()), player);
		}
		return false;
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
		breakBlock(fairy);
	}

	@Override
	public void onEnd(EntityFairy fairy) {

	}

	@Override
	public void onConfigure(EntityFairy fairy, @Nullable BlockPos targetBlock, @Nullable Entity targetEntity, Vec3d lookVec) {

	}
}
