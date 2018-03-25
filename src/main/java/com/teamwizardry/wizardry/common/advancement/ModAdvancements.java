package com.teamwizardry.wizardry.common.advancement;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.advancements.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.HashMap;

/**
 * Created by Demoniaque on 7/1/2016.
 */
public class ModAdvancements {

	public static Advancement BOOK;
	public static Advancement MANAPOOL;
	public static Advancement DEVILDUST;
	public static Advancement CRUNCH;

	public static AdvancementManager PAGE;

	public static void init() {
		MANAPOOL = new Advancement(
				new ResourceLocation(Wizardry.MODID, "advancement.manapool"),
				null,
				new DisplayInfo(
						new ItemStack(ModItems.ORB),
						new TextComponentTranslation("wizardry.advancement.begin.name"),
						new TextComponentTranslation("wizardry.advancement.begin.desc"),
						null, FrameType.GOAL, true, true, false),
				AdvancementRewards.EMPTY, new HashMap<>(), new String[0][0]);

		//	ModAdvancement("manapool", 1, -2, ModItems.ORB, null);
		//	BOOK = new ModAdvancement("book", 3, 0, ModItems.BOOK, MANAPOOL);
		//	DEVILDUST = new ModAdvancement("devildust", -1, 0, ModItems.DEVIL_DUST, null);
		//	CRUNCH = new ModAdvancement("crunch", 1, 2, Blocks.BEDROCK, null);
//
		//	PAGE = new AchievementPage(Wizardry.MODNAME, ModAdvancement.achievements.toArray(new Achievement[ModAdvancement.achievements.size()]));
		//	AchievementPage.registerAchievementPage(PAGE);
//
	}
}
