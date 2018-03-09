package com.teamwizardry.wizardry.api.item.halo;

import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class HaloInfusionItemRegistry {

	public static HaloInfusionItemRegistry INSTANCE = new HaloInfusionItemRegistry();

	public static final HaloInfusionItem EMPTY;
	public static final HaloInfusionItem OVERWORLD_RABBIT_FOOT;
	public static final HaloInfusionItem OVERWORLD_PRISMARINE_CRYSTALS;
	public static final HaloInfusionItem OVERWORLD_EMERALD;
	public static final HaloInfusionItem NETHER_BLAZE_POWDERS;
	public static final HaloInfusionItem NETHER_GHAST_TEARS;
	public static final HaloInfusionItem NETHER_NETHER_STAR;
	public static final HaloInfusionItem END_POPPED_CHORUS;
	public static final HaloInfusionItem END_DRAGON_BREATH;
	public static final HaloInfusionItem END_SHULKER_SHELL;
	public static final HaloInfusionItem UNDERWORLD_FAIRY_DUST;
	public static final HaloInfusionItem UNDEROWRLD_UNICORN_HORN;
	private static final List<HaloInfusionItem> items = new ArrayList<>();

	static {
		addHaloInfusionItem(EMPTY = new HaloInfusionItem(ItemStack.EMPTY));

		addHaloInfusionItem(OVERWORLD_RABBIT_FOOT = new HaloInfusionItem(Items.RABBIT_FOOT, 2));
		addHaloInfusionItem(OVERWORLD_PRISMARINE_CRYSTALS = new HaloInfusionItem(Items.PRISMARINE_CRYSTALS, 4));
		addHaloInfusionItem(OVERWORLD_EMERALD = new HaloInfusionItem(Items.EMERALD));

		addHaloInfusionItem(NETHER_BLAZE_POWDERS = new HaloInfusionItem(Items.BLAZE_ROD, 5));
		addHaloInfusionItem(NETHER_GHAST_TEARS = new HaloInfusionItem(Items.GHAST_TEAR, 4));
		addHaloInfusionItem(NETHER_NETHER_STAR = new HaloInfusionItem(Items.NETHER_STAR));

		addHaloInfusionItem(END_POPPED_CHORUS = new HaloInfusionItem(Items.CHORUS_FRUIT_POPPED));
		addHaloInfusionItem(END_DRAGON_BREATH = new HaloInfusionItem(Items.DRAGON_BREATH));
		addHaloInfusionItem(END_SHULKER_SHELL = new HaloInfusionItem(Items.SHULKER_SHELL));

		addHaloInfusionItem(UNDERWORLD_FAIRY_DUST = new HaloInfusionItem(ModItems.FAIRY_DUST));
		addHaloInfusionItem(UNDEROWRLD_UNICORN_HORN = new HaloInfusionItem(ModItems.UNICORN_HORN));
	}

	public static void addHaloInfusionItem(HaloInfusionItem item) {
		items.add(item);
	}

	public static List<HaloInfusionItem> getItems() {
		return items;
	}

	public static HaloInfusionItem getItemFromName(String nbtName) {
		for (HaloInfusionItem item : items) {
			if (item.getNbtName().equals(nbtName)) return item;
		}
		return HaloInfusionItemRegistry.EMPTY;
	}
}
