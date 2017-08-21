package com.teamwizardry.wizardry.common.block.wisdomwood;

import com.teamwizardry.librarianlib.core.common.OreDictionaryRegistrar;
import com.teamwizardry.librarianlib.features.base.block.BlockModLog;
import net.minecraft.block.SoundType;

/**
 * Created by LordSaad.
 */
public class BlockWisdomWoodLog extends BlockModLog {

	public BlockWisdomWoodLog() {
		super("wisdom_wood_log");
		OreDictionaryRegistrar.registerOre("logWood", this);
		setSoundType(SoundType.WOOD);
	}
}
