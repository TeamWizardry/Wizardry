package com.teamwizardry.wizardry.common.lib;

import com.teamwizardry.librarianlib.foundation.registration.LazyTileEntityType;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
import com.teamwizardry.wizardry.common.tile.TileManaBattery;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class LibTileEntityType {
	public static final LazyTileEntityType<TileCraftingPlate> CRAFTING_PLATE = new LazyTileEntityType<>();
	public static final LazyTileEntityType<TileManaBattery> MANA_BATTERY = new LazyTileEntityType<>();
}
