package com.teamwizardry.wizardry.common.block.wisdomwood;


import com.teamwizardry.librarianlib.features.base.ModCreativeTab;
import com.teamwizardry.librarianlib.features.base.block.BlockModPlanks;
import com.teamwizardry.wizardry.Wizardry;

import javax.annotation.Nullable;

/**
 * Created by LordSaad.
 */
public class BlockWisdomWoodPlanks extends BlockModPlanks {

	public BlockWisdomWoodPlanks() {
		super("wisdom_wood_planks");
	}

	@Nullable
	@Override
	public ModCreativeTab getCreativeTab() {
		return Wizardry.tab;
	}
}
