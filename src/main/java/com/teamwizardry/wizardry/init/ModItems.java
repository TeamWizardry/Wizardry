package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.common.item.*;
import com.teamwizardry.wizardry.common.item.dusts.ItemBomb;
import com.teamwizardry.wizardry.common.item.dusts.ItemDevilDust;
import com.teamwizardry.wizardry.common.item.dusts.ItemFairyDust;
import com.teamwizardry.wizardry.common.item.dusts.ItemSkyDust;
import com.teamwizardry.wizardry.common.item.halos.*;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Loader;

/**
 * Created by Demoniaque on 4/9/2016.
 */
public class ModItems {

	//public static ItemGlassOrb GLASS_ORB;
	public static ItemOrb ORB;
	public static ItemNacrePearl PEARL_NACRE;

	public static ItemStaff STAFF;

	public static ItemRing RING;
	public static ItemBook BOOK;
	public static ItemSyringe SYRINGE;
	public static ItemBomb BOMB;
	public static ItemJar JAR;
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

	public static ItemMagicWand MAGIC_WAND;

	public static ItemWisdomStick WISDOM_STICK;

	public static void init() {
		//GLASS_ORB = new ItemGlassOrb();
		ORB = new ItemOrb();
		PEARL_NACRE = new ItemNacrePearl();

		RING = new ItemRing();
		BOOK = new ItemBook();
		MAGIC_WAND = new ItemMagicWand();
		SYRINGE = new ItemSyringe();
		BOMB = new ItemBomb();
		BLACKENED_SPIRIT = new ItemBlackenedSpirit();

		STAFF = new ItemStaff();

		FAIRY_WINGS = new ItemFairyWings();
		FAIRY_IMBUED_APPLE = new ItemFairyImbuedApple();

		FAIRY_DUST = new ItemFairyDust();
		DEVIL_DUST = new ItemDevilDust();
		SKY_DUST = new ItemSkyDust();

		UNICORN_HORN = new ItemUnicornHorn();

		WISDOM_STICK = new ItemWisdomStick();

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
