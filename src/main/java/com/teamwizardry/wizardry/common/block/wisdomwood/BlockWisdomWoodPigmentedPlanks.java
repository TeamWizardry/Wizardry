package com.teamwizardry.wizardry.common.block.wisdomwood;

import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import com.teamwizardry.librarianlib.common.base.block.BlockModPlanks;
import com.teamwizardry.wizardry.Wizardry;
import org.jetbrains.annotations.Nullable;

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
