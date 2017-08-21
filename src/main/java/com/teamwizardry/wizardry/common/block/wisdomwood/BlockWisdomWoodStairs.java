package com.teamwizardry.wizardry.common.block.wisdomwood;

import com.teamwizardry.librarianlib.core.common.OreDictionaryRegistrar;
import com.teamwizardry.librarianlib.features.base.block.BlockModStairs;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.block.SoundType;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by LordSaad.
 */
public class BlockWisdomWoodStairs extends BlockModStairs {

	public BlockWisdomWoodStairs() {
		super("wisdom_wood_stairs", ModBlocks.WISDOM_WOOD_PLANKS.getDefaultState());
		OreDictionaryRegistrar.registerOre("stairWood", this);
		setSoundType(SoundType.WOOD);
	}
}
