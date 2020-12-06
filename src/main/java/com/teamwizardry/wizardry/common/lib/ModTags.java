package com.teamwizardry.wizardry.common.lib;

import static com.teamwizardry.wizardry.Wizardry.MODID;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class ModTags {

	@SuppressWarnings("unused")
    private static Tag<Item> tag(String name) {
		return new ItemTags.Wrapper(new ResourceLocation(MODID, name));
	}

	@SuppressWarnings("unused")
    private static Tag<Item> forgeTag(String name) {
		return new ItemTags.Wrapper(new ResourceLocation("forge", name));
	}

	public static class Fluids {
		public static final Tag<Fluid> MANA = tag("mana_fluid");

		private static Tag<Fluid> tag(String name) {
			return new FluidTags.Wrapper(new ResourceLocation(MODID, name));
		}
	}
}
