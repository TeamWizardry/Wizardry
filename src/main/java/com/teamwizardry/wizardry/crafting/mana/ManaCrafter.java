package com.teamwizardry.wizardry.crafting.mana;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public abstract class ManaCrafter {
	protected String name;
	protected int maxDuration;
	protected int currentDuration;

	public ManaCrafter(String name, int maxDuration) {
		this.name = name;
		this.maxDuration = maxDuration;
		this.currentDuration = 0;
	}

	public abstract boolean isValid(World world, BlockPos pos, List<EntityItem> items);

	public void tick(World world, BlockPos pos, List<EntityItem> items) {
		currentDuration++;
	}

	public abstract void finish(World world, BlockPos pos, List<EntityItem> items);

	public boolean isFinished() {
		return currentDuration >= maxDuration;
	}

	public boolean equals(ManaCrafter other) {
		return other.name.equals(name);
	}

	public boolean isInstant() {
		return maxDuration == 0;
	}
}
