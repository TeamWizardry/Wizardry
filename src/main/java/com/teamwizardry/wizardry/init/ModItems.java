package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.common.item.*;
import com.teamwizardry.wizardry.common.item.dusts.ItemDevilDust;
import com.teamwizardry.wizardry.common.item.dusts.ItemFairyDust;
import com.teamwizardry.wizardry.common.item.dusts.ItemSkyDust;
import com.teamwizardry.wizardry.common.item.halos.*;
import com.teamwizardry.wizardry.common.item.mob.ItemBlackenedSpirit;
import com.teamwizardry.wizardry.common.item.mob.ItemFairyWings;
import com.teamwizardry.wizardry.common.item.mob.ItemUnicornHorn;
import com.teamwizardry.wizardry.common.item.pearlbelt.ItemPearlBelt;
import com.teamwizardry.wizardry.common.item.tools.ItemUnicornDagger;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Loader;

//import com.teamwizardry.wizardry.common.item.pearlbelt.ItemPearlBelt;

/**
 * Created by Demoniaque on 4/9/2016.
 */
public class ModItems {

	public static ToolMaterial UNICORN_HORN_MAT = EnumHelper.addToolMaterial("unicorn_horn", 0, 250, 0, -3, 15);
	
	public static ItemOrb ORB;
	public static ItemLevitationOrb LEVITATION_ORB;
	public static NacrePearlSpell PEARL_NACRE;

	public static ItemStaff STAFF;

	public static ItemRing RING;
	public static ItemBook BOOK;
	public static ItemSyringe SYRINGE;
	public static ItemBomb BOMB;
	public static ItemJar JAR_ITEM;
	public static ItemUnicornHorn UNICORN_HORN;
	public static ItemBlackenedSpirit BLACKENED_SPIRIT;
	public static Item CAPE;

	public static ItemFairyWings FAIRY_WINGS;
	public static ItemFairyImbuedApple FAIRY_IMBUED_APPLE;

	public static ItemFairyDust FAIRY_DUST;
	public static ItemDevilDust DEVIL_DUST;
	public static ItemSkyDust SKY_DUST;

	public static Item FAKE_HALO;
	public static Item REAL_HALO;
	public static Item CREATIVE_HALO;

	public static Item PEARL_BELT;
	
	public static ItemUnicornDagger UNICORN_DAGGER;

	public static ItemMagicWand MAGIC_WAND;

	public static ItemWisdomStick WISDOM_STICK;

	public static void init() {
		ORB = new ItemOrb();
		LEVITATION_ORB = new ItemLevitationOrb();
		PEARL_NACRE = new NacrePearlSpell();

		RING = new ItemRing();
		BOOK = new ItemBook();
		MAGIC_WAND = new ItemMagicWand();
		SYRINGE = new ItemSyringe();
		BOMB = new ItemBomb();
		BLACKENED_SPIRIT = new ItemBlackenedSpirit();
		JAR_ITEM = new ItemJar();

		STAFF = new ItemStaff();

		FAIRY_WINGS = new ItemFairyWings();
		FAIRY_IMBUED_APPLE = new ItemFairyImbuedApple();

		FAIRY_DUST = new ItemFairyDust();
		DEVIL_DUST = new ItemDevilDust();
		SKY_DUST = new ItemSkyDust();

		UNICORN_HORN = new ItemUnicornHorn();
		UNICORN_DAGGER = new ItemUnicornDagger();

		WISDOM_STICK = new ItemWisdomStick();

		PEARL_BELT = new ItemPearlBelt();

		if (Loader.isModLoaded("baubles")) {
			CAPE = new ItemCapeBauble();
			FAKE_HALO = new ItemFakeHaloBauble();
			REAL_HALO = new ItemRealHaloBauble();
			CREATIVE_HALO = new ItemCreativeHaloBauble();
		} else {
			CAPE = new ItemCapeChest();
			FAKE_HALO = new ItemFakeHaloHead();
			REAL_HALO = new ItemRealHaloHead();
			CREATIVE_HALO = new ItemCreativeHaloHead();
		}
	}
}
