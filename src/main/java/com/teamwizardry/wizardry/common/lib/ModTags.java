package com.teamwizardry.wizardry.common.lib;

import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class ModTags {

	private static Tag<Item> tag(String name) {
		return new ItemTags.Wrapper(new ResourceLocation(Wizardry.MODID, name));
	}

	private static Tag<Item> forgeTag(String name) {
		return new ItemTags.Wrapper(new ResourceLocation("forge", name));
	}
}
