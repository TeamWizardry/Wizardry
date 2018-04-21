package com.teamwizardry.wizardry.common.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class UnicornPathFinder extends PathNavigate {

	public UnicornPathFinder(EntityLiving entityIn, World worldIn) {
		super(entityIn, worldIn);
	}

	@Override
	protected PathFinder getPathFinder() {
		return null;
	}

	@Override
	protected Vec3d getEntityPosition() {
		return null;
	}

	@Override
	protected boolean canNavigate() {
		return false;
	}

	@Override
	protected boolean isDirectPathBetweenPoints(Vec3d posVec31, Vec3d posVec32, int sizeX, int sizeY, int sizeZ) {
		return false;
	}

	@Override
	public void setSpeed(double speedIn) {
		super.setSpeed(speedIn);
	}

	@Override
	public float getPathSearchRange() {
		return super.getPathSearchRange();
	}

	@Override
	public boolean canUpdatePathOnTimeout() {
		return super.canUpdatePathOnTimeout();
	}

	@Override
	public void updatePath() {
		super.updatePath();
	}

	@Nullable
	@Override
	public Path getPathToPos(BlockPos pos) {
		return super.getPathToPos(pos);
	}

	@Nullable
	@Override
	public Path getPathToEntityLiving(Entity entityIn) {
		return super.getPathToEntityLiving(entityIn);
	}

	@Override
	public boolean tryMoveToXYZ(double x, double y, double z, double speedIn) {
		return super.tryMoveToXYZ(x, y, z, speedIn);
	}

	@Override
	public boolean tryMoveToEntityLiving(Entity entityIn, double speedIn) {
		return super.tryMoveToEntityLiving(entityIn, speedIn);
	}

	@Override
	public boolean setPath(@Nullable Path pathentityIn, double speedIn) {
		return super.setPath(pathentityIn, speedIn);
	}

	@Nullable
	@Override
	public Path getPath() {
		return super.getPath();
	}

	@Override
	public void onUpdateNavigation() {
		super.onUpdateNavigation();
	}

	@Override
	protected void debugPathFinding() {
		super.debugPathFinding();
	}

	@Override
	protected void pathFollow() {
		super.pathFollow();
	}

	@Override
	protected void checkForStuck(Vec3d positionVec3) {
		super.checkForStuck(positionVec3);
	}

	@Override
	public boolean noPath() {
		return super.noPath();
	}

	@Override
	public void clearPath() {
		super.clearPath();
	}

	@Override
	protected boolean isInLiquid() {
		return super.isInLiquid();
	}

	@Override
	protected void removeSunnyPath() {
		super.removeSunnyPath();
	}

	@Override
	public boolean canEntityStandOnPos(BlockPos pos) {
		return super.canEntityStandOnPos(pos);
	}

	@Override
	public NodeProcessor getNodeProcessor() {
		return super.getNodeProcessor();
	}
}
