package com.teamwizardry.wizardry.common.block.wisdomwood;

import com.teamwizardry.librarianlib.features.base.block.BlockModFenceGate;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.block.SoundType;

/**
 * Created by Demoniaque.
 */
public class BlockWisdomWoodFenceGate extends BlockModFenceGate {

	public BlockWisdomWoodFenceGate() {
		super("wisdom_wood_fence_gate", ModBlocks.WISDOM_WOOD_PLANKS.getDefaultState());
		setSoundType(SoundType.WOOD);
	}
}
