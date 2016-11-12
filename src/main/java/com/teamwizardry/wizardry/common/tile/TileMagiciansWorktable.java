package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import net.minecraft.util.math.BlockPos;

/**
 * Created by LordSaad.
 */
@TileRegister("magicians_worktable")
public class TileMagiciansWorktable extends TileMod {

	@Save
	public BlockPos linkedTable;
}
