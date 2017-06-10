package com.teamwizardry.wizardry.common.block.wisdomwood;

import com.teamwizardry.librarianlib.features.base.block.BlockModStairs;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by LordSaad.
 */
public class BlockWisdomWoodStairs extends BlockModStairs {

	public BlockWisdomWoodStairs() {
		super("wisdom_wood_stairs", ModBlocks.WISDOM_WOOD_PLANKS.getDefaultState());
		OreDictionary.registerOre("stairWood", this);
	}
}
