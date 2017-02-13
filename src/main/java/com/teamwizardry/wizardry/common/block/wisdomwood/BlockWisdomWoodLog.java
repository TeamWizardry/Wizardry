package com.teamwizardry.wizardry.common.block.wisdomwood;

import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import com.teamwizardry.librarianlib.common.base.block.BlockModLog;
import com.teamwizardry.wizardry.Wizardry;
import org.jetbrains.annotations.Nullable;

/**
 * Created by LordSaad.
 */
public class BlockWisdomWoodLog extends BlockModLog {

	public BlockWisdomWoodLog() {
		super("wisdom_wood_log");
		setCreativeTab(Wizardry.tab);
	}

	@Nullable
	@Override
	public ModCreativeTab getCreativeTab() {
		return Wizardry.tab;
	}
}
