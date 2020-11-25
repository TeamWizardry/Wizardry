package com.teamwizardry.wizardry.common.lib;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
import com.teamwizardry.wizardry.common.tile.TileMagicWorktable;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Wizardry.MODID)
public class LibTileEntityType {
	public static final TileEntityType<TileMagicWorktable> MAGICIANS_WORKTABLE = null;
	public static final TileEntityType<TileCraftingPlate> CRAFTING_PLATE = null;
}
