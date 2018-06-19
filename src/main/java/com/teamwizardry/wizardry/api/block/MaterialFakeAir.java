package com.teamwizardry.wizardry.api.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class MaterialFakeAir extends Material {

	public MaterialFakeAir() {
		super(MapColor.AIR);
	}

	@Override
	public boolean isReplaceable() {
		return false;
	}

	@Override
	public boolean isSolid() {
		return false;
	}

	@Override
	public boolean blocksLight() {
		return false;
	}

	@Override
	public boolean blocksMovement() {
		return false;
	}
}
