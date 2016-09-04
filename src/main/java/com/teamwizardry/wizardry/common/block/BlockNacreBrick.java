package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import com.teamwizardry.librarianlib.common.base.block.BlockMod;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Saad on 9/3/2016.
 */
public class BlockNacreBrick extends BlockMod {

	public BlockNacreBrick() {
		super("nacre_block_brick", Material.ROCK);
		setHardness(0.6f);
		setSoundType(SoundType.STONE);
	}

	@Nullable
	@Override
	public ModCreativeTab getCreativeTab() {
		return Wizardry.tab;
	}
}