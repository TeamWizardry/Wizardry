package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.api.block.TileCachable;
import net.minecraft.item.ItemStack;

/**
 * Created by Demoniaque.
 */
@TileRegister("mana_magnet")
public class TileManaMagnet extends TileCachable {

	@Save
	public ItemStack manaOrb;
}
