package com.teamwizardry.wizardry.common.block.wisdomwood;

import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import com.teamwizardry.librarianlib.common.base.block.BlockModPlanks;
import com.teamwizardry.wizardry.Wizardry;
import org.jetbrains.annotations.Nullable;

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
