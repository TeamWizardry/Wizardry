package com.teamwizardry.wizardry.common;

import com.teamwizardry.librarianlib.common.structure.Structure;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.util.ResourceLocation;

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

