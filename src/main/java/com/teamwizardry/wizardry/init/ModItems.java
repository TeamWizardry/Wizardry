package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.common.item.*;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Loader;

/**
 * Created by Saad on 4/9/2016.
 */
public class ModItems {

	public static ItemGlassOrb GLASS_ORB;
	public static ItemManaOrb MANA_ORB;
	public static ItemNacrePearl PEARL_NACRE;

	public static ItemStaff STAFF;

	public static ItemRing RING;
	public static ItemBook BOOK;
	public static ItemDevilDust DEVIL_DUST;
	public static ItemSyringe SYRINGE;

	public static ItemFairyWings FAIRY_WINGS;
	public static ItemFairyDust FAIRY_DUST;
	public static ItemFairyImbuedApple FAIRY_IMBUED_APPLE;

	public static ItemJar JAR;
	public static ItemUnicornHorn UNICORN_HORN;
	public static Item CAPE;

	public static ItemMagicWand MAGIC_WAND;

	public static void init() {
		GLASS_ORB = new ItemGlassOrb();
		MANA_ORB = new ItemManaOrb();
		PEARL_NACRE = new ItemNacrePearl();

		RING = new ItemRing();
		BOOK = new ItemBook();
		MAGIC_WAND = new ItemMagicWand();
		DEVIL_DUST = new ItemDevilDust();
		SYRINGE = new ItemSyringe();

		STAFF = new ItemStaff();

		FAIRY_WINGS = new ItemFairyWings();
		FAIRY_DUST = new ItemFairyDust();
		FAIRY_IMBUED_APPLE = new ItemFairyImbuedApple();

		UNICORN_HORN = new ItemUnicornHorn();

		if (Loader.isModLoaded("baubles")) CAPE = new ItemCapeBauble();
		else CAPE = new ItemCapeChest();
	}
}
