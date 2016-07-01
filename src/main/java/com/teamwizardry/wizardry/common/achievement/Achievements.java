package com.teamwizardry.wizardry.common.achievement;

import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.stats.Achievement;

/**
 * Created by Saad on 7/1/2016.
 */
public class Achievements {

    public static Achievement PHYSICSBOOK;

    public static void init() {
        PHYSICSBOOK = new Achievement("physicsBook", "physicsBook", 0, 0, ModItems.PHYSICS_BOOK, null);
        PHYSICSBOOK.registerStat();
    }
}
