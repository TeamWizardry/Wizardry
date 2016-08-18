package com.teamwizardry.wizardry.init;


import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.teamwizardry.wizardry.common.block.BlockCraftingPlate;
import com.teamwizardry.wizardry.common.block.BlockMagiciansWorktable;
import com.teamwizardry.wizardry.common.block.BlockManaBattery;
import com.teamwizardry.wizardry.common.block.BlockPedestal;
import com.teamwizardry.wizardry.common.block.MaterialNacre;

/**
 * Created by Saad on 3/24/2016.
 */
public class ModBlocks {

    public static Material NACRE_MATERIAL = new MaterialNacre(MapColor.WATER);

    public static BlockCraftingPlate CRAFTING_PLATE;
    public static BlockMagiciansWorktable MAGICIANS_WORKTABLE;
    public static BlockManaBattery MANA_BATTERY;
    public static BlockPedestal PEDESTAL;

    public static void init() {
        CRAFTING_PLATE = new BlockCraftingPlate();
        MAGICIANS_WORKTABLE = new BlockMagiciansWorktable();
        MANA_BATTERY = new BlockManaBattery();
        PEDESTAL = new BlockPedestal();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        CRAFTING_PLATE.initModel();
        MAGICIANS_WORKTABLE.initModel();
        MANA_BATTERY.initModel();
        PEDESTAL.initModel();
    }
}
