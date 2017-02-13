package com.teamwizardry.wizardry.common.block.wisdomwood;

import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import com.teamwizardry.librarianlib.common.base.block.BlockModSlab;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.init.ModBlocks;
import org.jetbrains.annotations.Nullable;

/**
 * Created by LordSaad.
 */
public class BlockWisdomWoodSlab extends BlockModSlab {

	public BlockWisdomWoodSlab() {
		super("wisdom_wood_slab", ModBlocks.WISDOM_WOOD_PLANKS.getDefaultState());
	}

	@Nullable
	@Override
	public ModCreativeTab getCreativeTab() {
		return Wizardry.tab;
	}
}
