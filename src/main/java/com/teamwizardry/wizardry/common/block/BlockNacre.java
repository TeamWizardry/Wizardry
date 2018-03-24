package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.librarianlib.features.base.block.BlockMod;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

/**
 * Created by Demoniaque on 9/3/2016.
 */
public class BlockNacre extends BlockMod {

	public BlockNacre() {
		super("nacre_block", Material.ROCK);
		setHardness(0.5f);
		setSoundType(SoundType.STONE);
	}
}
