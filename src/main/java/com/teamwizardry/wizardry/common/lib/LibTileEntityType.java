package com.teamwizardry.wizardry.common.lib;

import com.teamwizardry.librarianlib.foundation.registration.LazyTileEntityType;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
import com.teamwizardry.wizardry.common.tile.TileManaBattery;

public class LibTileEntityType {
	public static final LazyTileEntityType<TileCraftingPlate> CRAFTING_PLATE = new LazyTileEntityType<>();
	public static final LazyTileEntityType<TileManaBattery> MANA_BATTERY = new LazyTileEntityType<>();
}
