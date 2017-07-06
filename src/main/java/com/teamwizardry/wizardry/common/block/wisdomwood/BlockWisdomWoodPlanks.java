package com.teamwizardry.wizardry.common.block.wisdomwood;


import com.teamwizardry.librarianlib.features.base.block.BlockModPlanks;
import net.minecraft.block.SoundType;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by LordSaad.
 */
public class BlockWisdomWoodPlanks extends BlockModPlanks {

	public BlockWisdomWoodPlanks() {
		super("wisdom_wood_planks");
		OreDictionary.registerOre("plankWood", this);
		setSoundType(SoundType.WOOD);
	}
}
