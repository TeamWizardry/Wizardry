package com.teamwizardry.wizardry.api.item;

import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class HaloInfusionItemRegistry {

	public static HaloInfusionItemRegistry INSTANCE = new HaloInfusionItemRegistry();

	public final List<HaloInfusionItem> items = new ArrayList<>();

	public final HaloInfusionItem OVERWORLD_RABBIT_FOOT;
	public final HaloInfusionItem OVERWORLD_PRISMARINE_CRYSTALS;
	public final HaloInfusionItem OVERWORLD_EMERALD;
	public final HaloInfusionItem NETHER_BLAZE_POWDERS;
	public final HaloInfusionItem NETHER_GHAST_TEARS;
	public final HaloInfusionItem NETHER_NETHER_STAR;
	public final HaloInfusionItem END_POPPED_CHORUS;
	public final HaloInfusionItem END_DRAGON_BREATH;
	public final HaloInfusionItem END_SHULKER_SHELL;
	public final HaloInfusionItem UNDERWORLD_FAIRY_DUST;
	public final HaloInfusionItem UNDEROWRLD_UNICORN_HORN;

	public HaloInfusionItemRegistry() {
		items.add(OVERWORLD_RABBIT_FOOT = new HaloInfusionItem(Items.RABBIT_FOOT, 2));
		items.add(OVERWORLD_PRISMARINE_CRYSTALS = new HaloInfusionItem(Items.PRISMARINE_CRYSTALS, 4));
		items.add(OVERWORLD_EMERALD = new HaloInfusionItem(Items.EMERALD));

		items.add(NETHER_BLAZE_POWDERS = new HaloInfusionItem(Items.BLAZE_ROD, 5));
		items.add(NETHER_GHAST_TEARS = new HaloInfusionItem(Items.GHAST_TEAR, 4));
		items.add(NETHER_NETHER_STAR = new HaloInfusionItem(Items.NETHER_STAR));

		items.add(END_POPPED_CHORUS = new HaloInfusionItem(Items.CHORUS_FRUIT_POPPED));
		items.add(END_DRAGON_BREATH = new HaloInfusionItem(Items.DRAGON_BREATH));
		items.add(END_SHULKER_SHELL = new HaloInfusionItem(Items.SHULKER_SHELL));

		items.add(UNDERWORLD_FAIRY_DUST = new HaloInfusionItem(ModItems.FAIRY_DUST));
		items.add(UNDEROWRLD_UNICORN_HORN = new HaloInfusionItem(ModItems.UNICORN_HORN));
	}

	public static class HaloInfusionItem {

		private final ItemStack stack;
		private final String nbtName;

		public HaloInfusionItem(ItemStack stack) {
			this.stack = stack;
			this.nbtName = stack.getUnlocalizedName();
		}

		public HaloInfusionItem(Item item) {
			this(item, 1);
		}

		public HaloInfusionItem(Item item, int count) {
			this(new ItemStack(item, count));
		}

		public ItemStack getStack() {
			return stack;
		}

		public String getNbtName() {
			return nbtName;
		}
	}
}
