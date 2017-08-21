package com.teamwizardry.wizardry.common.block.wisdomwood;


import com.teamwizardry.librarianlib.core.common.OreDictionaryRegistrar;
import com.teamwizardry.librarianlib.features.base.block.BlockModPlanks;
import net.minecraft.block.SoundType;

/**
 * Created by LordSaad.
 */
public class BlockWisdomWoodPlanks extends BlockModPlanks {

	public BlockWisdomWoodPlanks() {
		super("wisdom_wood_planks");
		OreDictionaryRegistrar.registerOre("plankWood", this);
		setSoundType(SoundType.WOOD);
	}
}
