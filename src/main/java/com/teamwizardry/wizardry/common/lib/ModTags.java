package com.teamwizardry.wizardry.common.lib;

import static com.teamwizardry.wizardry.Wizardry.MODID;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

public class ModTags {

	@SuppressWarnings("unused")
    private static ITag<Item> tag(String name) {
		return ItemTags.createOptional(new ResourceLocation(MODID, name));
	}

	@SuppressWarnings("unused")
    private static ITag<Item> forgeTag(String name) {
		return ItemTags.createOptional(new ResourceLocation("forge", name));
	}

	public static class Fluids {
		public static final ITag<Fluid> MANA = tag("mana_fluid");

		private static ITag<Fluid> tag(String name) {
			return FluidTags.createOptional(new ResourceLocation(MODID, name));
		}
	}
}
