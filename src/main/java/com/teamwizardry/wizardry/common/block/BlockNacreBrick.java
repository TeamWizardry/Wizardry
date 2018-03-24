package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.librarianlib.features.base.block.BlockMod;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

/**
 * Created by Demoniaque on 9/3/2016.
 */
public class BlockNacreBrick extends BlockMod {

	public BlockNacreBrick() {
		super("nacre_block_brick", Material.ROCK);
		setHardness(0.6f);
		setSoundType(SoundType.STONE);
	}
}
