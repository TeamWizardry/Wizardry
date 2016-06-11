package me.lordsaad.wizardry;

import me.lordsaad.wizardry.items.ItemPearl;

/**
 * Created by Saad on 4/9/2016.
 */
public class ModItems {

    public static ItemPearl pearl;

    public static void init() {
        pearl = new ItemPearl();
    }

    public static void initModels() {
        pearl.initModel();
//        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new ItemPearl.ColorHandler(), pearl);
    }
}
