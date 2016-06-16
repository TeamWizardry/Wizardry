package me.lordsaad.wizardry;

import me.lordsaad.wizardry.items.ItemPearl;
import me.lordsaad.wizardry.items.ItemPhysicsBook;
import me.lordsaad.wizardry.items.ItemRing;
import net.minecraft.client.Minecraft;

/**
 * Created by Saad on 4/9/2016.
 */
public class ModItems {

    public static ItemPearl pearl;
    public static ItemRing ring;
    public static ItemPhysicsBook physicsBook;

    public static void init() {
        pearl = new ItemPearl();
        ring = new ItemRing();
        physicsBook = new ItemPhysicsBook();

    }

    public static void initModels() {
        pearl.initModel();
        physicsBook.initModel();
        ring.initModel();
    }

    public static void initColors() {
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new ItemPearl.ColorHandler(), pearl);
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new ItemRing.ColorHandler(), ring);
    }
}
