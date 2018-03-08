package com.teamwizardry.wizardry.common.block.wisdomwood;

import com.teamwizardry.librarianlib.features.base.block.BlockModFence;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.block.SoundType;

/**
 * Created by Demoniaque.
 */
public class BlockWisdomWoodFence extends BlockModFence {

	public BlockWisdomWoodFence() {
		super("wisdom_wood_fence", ModBlocks.WISDOM_WOOD_PLANKS.getDefaultState());
		setSoundType(SoundType.WOOD);
	}
}
