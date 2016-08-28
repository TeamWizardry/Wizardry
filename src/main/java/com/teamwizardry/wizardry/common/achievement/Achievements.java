package com.teamwizardry.wizardry.common.achievement;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

/**
 * Created by Saad on 7/1/2016.
 */
public class Achievements {

    public static Achievement PHYSICSBOOK;
    public static Achievement MANAPOOL;
    public static Achievement DEVILDUST;
    public static Achievement CRUNCH;

    public static AchievementPage PAGE;

    public static void init() {
        PHYSICSBOOK = new ModAchievement("physicsBook", 1, -2, ModItems.PHYSICS_BOOK, null);
        MANAPOOL = new ModAchievement("manapool", 1, 2, ModItems.PEARL_MANA, null);
        DEVILDUST = new ModAchievement("devildust", -1, 0, ModItems.DEVIL_DUST, null);
        CRUNCH = new ModAchievement("crunch", 3, 0, Blocks.BEDROCK, null);

        PAGE = new AchievementPage(Wizardry.MODNAME, ModAchievement.achievements.toArray(new Achievement[ModAchievement.achievements.size()]));
        AchievementPage.registerAchievementPage(PAGE);
    }
}
