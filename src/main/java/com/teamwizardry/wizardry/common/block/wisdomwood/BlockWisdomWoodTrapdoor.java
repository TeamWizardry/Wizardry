package com.teamwizardry.wizardry.common.block.wisdomwood;

import com.teamwizardry.librarianlib.features.base.ModCreativeTab;
import com.teamwizardry.librarianlib.features.base.block.BlockModTrapdoor;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.init.ModBlocks;

import javax.annotation.Nullable;

/**
 * Created by LordSaad.
 */
public class BlockWisdomWoodTrapdoor extends BlockModTrapdoor {

	public BlockWisdomWoodTrapdoor() {
		super("wisdom_wood_trapdoor", ModBlocks.WISDOM_WOOD_PLANKS.getDefaultState());
	}

	@Nullable
	@Override
	public ModCreativeTab getCreativeTab() {
		return Wizardry.tab;
	}
}
