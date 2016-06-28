package com.teamwizardry.wizardry.client.multiblock;

public enum Structures {
    INSTANCE;

    public static Structure craftingAltar;

    Structures() {
    }

    public static void reload() {
        craftingAltar = new Structure("crafting_altar");
    }
}
