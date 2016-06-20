package me.lordsaad.wizardry;

import me.lordsaad.wizardry.items.ItemDebugger;
import me.lordsaad.wizardry.items.ItemPhysicsBook;
import me.lordsaad.wizardry.items.ItemRing;
import me.lordsaad.wizardry.items.pearls.ItemGlassPearl;
import me.lordsaad.wizardry.items.pearls.ItemQuartzPearl;
import net.minecraft.client.Minecraft;

/**
 * Created by Saad on 4/9/2016.
 */
public class ModItems {

    public static ItemQuartzPearl quartzPearl;
    public static ItemGlassPearl glassPearl;
    public static ItemRing ring;
    public static ItemPhysicsBook physicsBook;

    public static ItemDebugger debug;

    public static void init() {
        quartzPearl = new ItemQuartzPearl();
        glassPearl = new ItemGlassPearl();
        ring = new ItemRing();
        physicsBook = new ItemPhysicsBook();
        debug = new ItemDebugger();
    }

    public static void initModels() {
        quartzPearl.initModel();
        glassPearl.initModel();
        physicsBook.initModel();
        ring.initModel();
        debug.initModel();
    }

    public static void initColors() {
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new ItemQuartzPearl.ColorHandler(), quartzPearl);
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new ItemRing.ColorHandler(), ring);
    }
}
