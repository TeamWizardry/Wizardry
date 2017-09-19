package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.util.RandUtil;

import net.minecraft.util.ITickable;

/**
 * Created by LordSaad.
 */
@TileRegister("light")
public class TileLight extends TileMod implements ITickable {

	@Override
	public void update() {
		ClientRunnable.run(() -> {
			if (RandUtil.nextInt(4) == 0) {
				Wizardry.proxy.tileLightParticles(world, pos);
			}
		});
	}
}
