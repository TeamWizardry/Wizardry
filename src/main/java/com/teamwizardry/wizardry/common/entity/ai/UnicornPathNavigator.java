package com.teamwizardry.wizardry.common.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class UnicornPathNavigator extends PathNavigateFlying {

	public UnicornPathNavigator(EntityLiving entityIn, World worldIn) {
		super(entityIn, worldIn);

		setCanFloat(true);
		setCanEnterDoors(true);
	}

	@Override
	public boolean canEntityStandOnPos(BlockPos pos) {
		return true;
	}
}
