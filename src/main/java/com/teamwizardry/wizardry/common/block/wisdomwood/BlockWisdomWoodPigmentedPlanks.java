package com.teamwizardry.wizardry.common.block.wisdomwood;

import com.teamwizardry.librarianlib.features.base.ModCreativeTab;
import com.teamwizardry.librarianlib.features.base.block.BlockModPlanks;
import com.teamwizardry.wizardry.Wizardry;

import javax.annotation.Nullable;

/**
 * Created by LordSaad.
 */
public class BlockWisdomWoodPigmentedPlanks extends BlockModPlanks {

	public BlockWisdomWoodPigmentedPlanks() {
		super("wisdom_wood_pigmented_planks");
	}

	@Nullable
	@Override
	public ModCreativeTab getCreativeTab() {
		return Wizardry.tab;
	}
}
