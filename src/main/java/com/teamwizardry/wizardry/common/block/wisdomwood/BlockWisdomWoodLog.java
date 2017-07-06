package com.teamwizardry.wizardry.common.block.wisdomwood;

import com.teamwizardry.librarianlib.features.base.block.BlockModLog;
import net.minecraft.block.SoundType;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by LordSaad.
 */
public class BlockWisdomWoodLog extends BlockModLog {

	public BlockWisdomWoodLog() {
		super("wisdom_wood_log");
		OreDictionary.registerOre("logWood", this);
		setSoundType(SoundType.WOOD);
	}
}
