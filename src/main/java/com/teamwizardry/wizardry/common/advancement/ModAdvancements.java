package com.teamwizardry.wizardry.common.advancement;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;

import java.util.HashMap;

/**
 * Created by Saad on 7/1/2016.
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
						new ItemStack(ModItems.MANA_ORB),
						new TextComponentString("Not a Mana Pool"),
						new TextComponentString("Discover a pool of liquid Mana in your travels"),
						null, FrameType.GOAL, true, true, false),
				AdvancementRewards.EMPTY, new HashMap<>(), new String[0][0]);


	//	ModAdvancement("manapool", 1, -2, ModItems.MANA_ORB, null);
	//	BOOK = new ModAdvancement("book", 3, 0, ModItems.BOOK, MANAPOOL);
	//	DEVILDUST = new ModAdvancement("devildust", -1, 0, ModItems.DEVIL_DUST, null);
	//	CRUNCH = new ModAdvancement("crunch", 1, 2, Blocks.BEDROCK, null);
//
	//	PAGE = new AchievementPage(Wizardry.MODNAME, ModAdvancement.achievements.toArray(new Achievement[ModAdvancement.achievements.size()]));
	//	AchievementPage.registerAchievementPage(PAGE);
//
	}
}
