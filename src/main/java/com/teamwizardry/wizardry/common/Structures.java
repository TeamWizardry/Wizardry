package com.teamwizardry.wizardry.common;

import com.teamwizardry.libarianlib.multiblock.Structure;

public enum Structures {
    INSTANCE;

    public static Structure craftingAltar;

    Structures() {
    	reload();
    }

    public static void reload() {
        craftingAltar = new Structure("crafting_altar");
    }
}

