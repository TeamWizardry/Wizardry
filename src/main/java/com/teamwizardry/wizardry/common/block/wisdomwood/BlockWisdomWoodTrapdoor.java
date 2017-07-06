package com.teamwizardry.wizardry.common.block.wisdomwood;

import com.teamwizardry.librarianlib.features.base.block.BlockModTrapdoor;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.block.SoundType;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by LordSaad.
 */
public class BlockWisdomWoodTrapdoor extends BlockModTrapdoor {

	public BlockWisdomWoodTrapdoor() {
		super("wisdom_wood_trapdoor", ModBlocks.WISDOM_WOOD_PLANKS.getDefaultState());
		OreDictionary.registerOre("trapDoorWood", this);
		setSoundType(SoundType.WOOD);
	}
}
