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

	public static ModAchievement BOOK;
	public static ModAchievement MANAPOOL;
	public static ModAchievement DEVILDUST;
	public static ModAchievement CRUNCH;

	public static AchievementPage PAGE;

	public static void init() {
		MANAPOOL = new ModAchievement("manapool", 1, -2, ModItems.MANA_ORB, null);
		BOOK = new ModAchievement("book", 3, 0, ModItems.BOOK, MANAPOOL);
		DEVILDUST = new ModAchievement("devildust", -1, 0, ModItems.DEVIL_DUST, null);
		CRUNCH = new ModAchievement("crunch", 1, 2, Blocks.BEDROCK, null);

		PAGE = new AchievementPage(Wizardry.MODNAME, ModAchievement.achievements.toArray(new Achievement[ModAchievement.achievements.size()]));
		AchievementPage.registerAchievementPage(PAGE);
	}
}
