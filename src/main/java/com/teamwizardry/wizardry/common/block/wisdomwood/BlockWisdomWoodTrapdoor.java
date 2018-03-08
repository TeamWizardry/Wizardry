package com.teamwizardry.wizardry.common.block.wisdomwood;

import com.teamwizardry.librarianlib.features.base.block.BlockModTrapdoor;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.block.SoundType;

/**
 * Created by Demoniaque.
 */
public class BlockWisdomWoodTrapdoor extends BlockModTrapdoor {

	public BlockWisdomWoodTrapdoor() {
		super("wisdom_wood_trapdoor", ModBlocks.WISDOM_WOOD_PLANKS.getDefaultState());
		setSoundType(SoundType.WOOD);
	}
}
