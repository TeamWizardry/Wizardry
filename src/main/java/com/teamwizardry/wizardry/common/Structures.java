package com.teamwizardry.wizardry.common;

import net.minecraft.util.ResourceLocation;

import com.teamwizardry.libarianlib.multiblock.Structure;
import com.teamwizardry.wizardry.Wizardry;

public enum Structures {
    INSTANCE;

    public static Structure craftingAltar;

    Structures() {
    	reload();
    }

    public static void reload() {
        craftingAltar = new Structure(new ResourceLocation(Wizardry.MODID, "crafting_altar"));
    }
}

