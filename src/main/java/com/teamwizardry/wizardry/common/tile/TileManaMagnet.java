package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.librarianlib.features.saving.Save;
import net.minecraft.item.ItemStack;

/**
 * Created by Demoniaque.
 */
@TileRegister("mana_magnet")
public class TileManaMagnet extends TileMod {

	@Save
	public ItemStack manaOrb;
}
