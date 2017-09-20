package com.teamwizardry.wizardry.common.entity.ai;

import com.teamwizardry.wizardry.api.util.RandUtil;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

import static net.minecraft.util.EnumFacing.UP;

public class EntityAIUnicornWander extends EntityAIWander {

	public EntityAIUnicornWander(EntityCreature creatureIn, double speedIn) {
		super(creatureIn, speedIn);
	}

	@Nullable
	@Override
	protected Vec3d getPosition() {
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(this.entity.getPosition().add(
				RandUtil.nextBoolean() ? RandUtil.nextInt(-20, -10) : RandUtil.nextInt(10, 20),
				0,
				RandUtil.nextBoolean() ? RandUtil.nextInt(-20, -10) : RandUtil.nextInt(10, 20)
		));
		pos.setPos(pos.getX(), RandUtil.nextInt(40, 70), pos.getZ());

		while (!this.entity.world.isAirBlock(pos)) {
			pos.move(UP);
		}

		return new Vec3d(pos).addVector(0.5, 0.5, 0.5);
	}
}
