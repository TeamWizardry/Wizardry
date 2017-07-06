package com.teamwizardry.wizardry.common.block.wisdomwood;

import com.teamwizardry.librarianlib.features.base.block.BlockModFence;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.block.SoundType;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by LordSaad.
 */
public class BlockWisdomWoodFence extends BlockModFence {

	public BlockWisdomWoodFence() {
		super("wisdom_wood_fence", ModBlocks.WISDOM_WOOD_PLANKS.getDefaultState());
		OreDictionary.registerOre("fenceWood", this);
		setSoundType(SoundType.WOOD);
	}
}
