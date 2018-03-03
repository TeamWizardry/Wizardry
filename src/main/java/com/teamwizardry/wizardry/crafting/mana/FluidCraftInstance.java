package com.teamwizardry.wizardry.crafting.mana;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

import java.util.List;

public abstract class FluidCraftInstance {
	protected String name;
	protected int maxDuration;
	protected int currentDuration;
	protected Fluid fluid;

	public FluidCraftInstance(String name, int maxDuration, Fluid fluid) {
		this.name = name;
		this.maxDuration = maxDuration;
		this.currentDuration = 0;
		this.fluid = fluid;
	}

	public Fluid getFluid() {
		return fluid;
	}

	public abstract boolean isValid(World world, BlockPos pos, List<EntityItem> items);

	public void tick(World world, BlockPos pos, List<EntityItem> items) {
		currentDuration++;
	}

	public abstract void finish(World world, BlockPos pos, List<EntityItem> items);

	public boolean isFinished() {
		return currentDuration >= maxDuration;
	}

	public boolean equals(FluidCraftInstance other) {
		return other.name.equals(name);
	}

	public boolean isInstant() {
		return maxDuration == 0;
	}
}
