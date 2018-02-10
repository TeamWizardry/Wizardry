package com.teamwizardry.wizardry.common.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLiquid;

/**
 * @author WireSegal
 * Created at 1:55 AM on 8/3/16.
 */
public class MaterialMana extends MaterialLiquid {
	public MaterialMana(MapColor color) {
		super(color);
		setNoPushMobility();
	}

	@Override
	public boolean blocksMovement() {
		return true;
	}
}
