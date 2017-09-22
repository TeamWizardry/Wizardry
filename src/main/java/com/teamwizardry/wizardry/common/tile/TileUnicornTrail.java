package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.librarianlib.features.saving.Save;
import net.minecraft.util.ITickable;

/**
 * Created by LordSaad.
 */
@TileRegister("unicorn_trail")
public class TileUnicornTrail extends TileMod implements ITickable {

	@Save
	public long savedTime = System.currentTimeMillis();

	@Override
	public void update() {
		if (System.currentTimeMillis() - savedTime >= 500) {
			getWorld().setBlockToAir(getPos());
		}
	}
}
