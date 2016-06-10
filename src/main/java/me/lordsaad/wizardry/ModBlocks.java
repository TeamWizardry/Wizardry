package me.lordsaad.wizardry;


import me.lordsaad.wizardry.blocks.BlockCraftingPlate;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Saad on 3/24/2016.
 */
public class ModBlocks {

    public static BlockCraftingPlate craftingPlate;

    public static void init() {
        craftingPlate = new BlockCraftingPlate();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        craftingPlate.initModel();
    }
}
