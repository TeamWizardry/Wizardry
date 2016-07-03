package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.common.item.*;
import com.teamwizardry.wizardry.common.item.pearl.ItemGlassPearl;
import com.teamwizardry.wizardry.common.item.pearl.ItemManaPearl;
import com.teamwizardry.wizardry.common.item.pearl.ItemNacrePearl;
import com.teamwizardry.wizardry.common.item.pearl.ItemQuartzPearl;
import net.minecraft.client.Minecraft;

/**
 * Created by Saad on 4/9/2016.
 */
public class ModItems {

    public static ItemQuartzPearl PEARL_QUARTZ;
    public static ItemGlassPearl PEARL_GLASS;
    public static ItemManaPearl PEARL_MANA;
    public static ItemNacrePearl PEARL_NACRE;

    public static ItemRing RING;
    public static ItemPhysicsBook PHYSICS_BOOK;
    public static ItemVinteumDust VINTEUM_DUST;
    public static ItemManaCake MANA_CAKE;

    public static ItemDebugger debug;

    public static void init() {
        PEARL_QUARTZ = new ItemQuartzPearl();
        PEARL_GLASS = new ItemGlassPearl();
        RING = new ItemRing();
        PHYSICS_BOOK = new ItemPhysicsBook();
        debug = new ItemDebugger();
        VINTEUM_DUST = new ItemVinteumDust();
        PEARL_MANA = new ItemManaPearl();
        PEARL_NACRE = new ItemNacrePearl();
        MANA_CAKE = new ItemManaCake();
    }

    public static void initModels() {
        PEARL_QUARTZ.initModel();
        PEARL_GLASS.initModel();
        PHYSICS_BOOK.initModel();
        RING.initModel();
        debug.initModel();
        VINTEUM_DUST.initModel();
        PEARL_MANA.initModel();
        PEARL_NACRE.initModel();
        MANA_CAKE.initModel();
    }

    public static void initColors() {
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new ItemQuartzPearl.ColorHandler(), PEARL_QUARTZ);
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new ItemNacrePearl.ColorHandler(), PEARL_NACRE);
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new ItemRing.ColorHandler(), RING);
    }
}
