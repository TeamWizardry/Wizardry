package me.lordsaad.wizardry;


import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Saad on 3/24/2016.
 */
public class ModBlocks {

    // public static BlockMirror mirror;
    // public static BlockPrism prism;
    // public static BlockMagnifier magnifier;

    public static void init() {
        //    mirror = new BlockMirror();
        //    prism = new BlockPrism();
        //    magnifier = new BlockMagnifier();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        //    mirror.initModel();
        //    prism.initModel();
        //    magnifier.initModel();
    }
}
