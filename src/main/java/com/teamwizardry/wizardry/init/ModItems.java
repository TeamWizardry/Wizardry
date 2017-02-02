package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.common.item.*;
import com.teamwizardry.wizardry.common.item.pearl.ItemGlassPearl;
import com.teamwizardry.wizardry.common.item.pearl.ItemManaPearl;
import com.teamwizardry.wizardry.common.item.pearl.ItemNacrePearl;

/**
 * Created by Saad on 4/9/2016.
 */
public class ModItems {

	public static ItemGlassPearl PEARL_GLASS;
	public static ItemManaPearl PEARL_MANA;
	public static ItemNacrePearl PEARL_NACRE;

	public static ItemStaff STAFF;

	public static ItemRing RING;
	public static ItemBook BOOK;
	public static ItemDevilDust DEVIL_DUST;
	public static ItemManaCake MANA_CAKE;
	public static ItemManaSteroid MANA_STEROID;

	public static ItemFairyWings FAIRY_WINGS;
	public static ItemFairyDust FAIRY_DUST;
	public static ItemFairyImbuedApple FAIRY_IMBUED_APPLE;

	public static ItemJar JAR;
	public static ItemUnicornHorn UNICORN_HORN;
	public static ItemCape CAPE;

	public static ItemDebugger DEBUG;

	public static void init() {
		PEARL_GLASS = new ItemGlassPearl();
		PEARL_MANA = new ItemManaPearl();
		PEARL_NACRE = new ItemNacrePearl();

		RING = new ItemRing();
		BOOK = new ItemBook();
		DEBUG = new ItemDebugger();
		DEVIL_DUST = new ItemDevilDust();
		MANA_CAKE = new ItemManaCake();
		MANA_STEROID = new ItemManaSteroid();

		STAFF = new ItemStaff();

		FAIRY_WINGS = new ItemFairyWings();
		FAIRY_DUST = new ItemFairyDust();
		FAIRY_IMBUED_APPLE = new ItemFairyImbuedApple();

		JAR = new ItemJar();
		UNICORN_HORN = new ItemUnicornHorn();
		CAPE = new ItemCape();
	}
}
