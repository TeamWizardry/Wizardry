package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import com.teamwizardry.librarianlib.common.base.block.BlockMod;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

import javax.annotation.Nullable;

/**
 * Created by Saad on 9/3/2016.
 */
public class BlockNacre extends BlockMod {

	public BlockNacre() {
		super("nacre_block", Material.ROCK);
		setHardness(0.5f);
		setSoundType(SoundType.STONE);
	}

	@Nullable
	@Override
	public ModCreativeTab getCreativeTab() {
		return Wizardry.tab;
	}
}
