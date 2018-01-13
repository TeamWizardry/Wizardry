package com.teamwizardry.wizardry.common.block.wisdomwood;

import com.teamwizardry.librarianlib.features.base.block.BlockModSlab;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.block.SoundType;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by LordSaad.
 */
public class BlockWisdomWoodSlab extends BlockModSlab {

	public BlockWisdomWoodSlab() {
		super("wisdom_wood_slab", ModBlocks.WISDOM_WOOD_PLANKS.getDefaultState());
		setSoundType(SoundType.WOOD);
		OreDictionary.registerOre("slabWood", this);
	}
}
