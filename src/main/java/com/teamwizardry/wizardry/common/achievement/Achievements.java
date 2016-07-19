package com.teamwizardry.wizardry.common.achievement;

import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.stats.Achievement;

/**
 * Created by Saad on 7/1/2016.
 */
public class Achievements {

    public static Achievement PHYSICSBOOK;
    public static Achievement MANAPOOL;
    public static Achievement DEVILDUST;

    public static void init() {
        PHYSICSBOOK = new ModAchievement("physicsBook", 0, 0, ModItems.PHYSICS_BOOK, null);
        MANAPOOL = new ModAchievement("manapool", 0, 1, ModItems.PEARL_MANA, null);
        DEVILDUST = new ModAchievement("devildust", 0, 1, ModItems.DEVIL_DUST, null);
    }
}
