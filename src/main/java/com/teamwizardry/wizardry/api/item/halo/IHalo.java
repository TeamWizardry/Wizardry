package com.teamwizardry.wizardry.api.item.halo;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public interface IHalo {

	default List<String> getHaloTooltip(ItemStack stack) {
		List<String> tooltips = new ArrayList<>();

		NBTTagList slots = ItemNBTHelper.getList(stack, "slots", NBTTagString.class);
		if (slots == null) {
			tooltips.add(TextFormatting.GRAY + "<EMPTY>");
			return tooltips;
		}

		for (int j = 0; j < slots.tagCount(); j++) {
			String string = slots.getStringTagAt(j);
			HaloInfusionItem infusionItem = HaloInfusionItemRegistry.getItemFromName(string);
			if (infusionItem != HaloInfusionItemRegistry.EMPTY) {
				tooltips.add(TextFormatting.GOLD + "- " + infusionItem.getStack().getDisplayName());
			}
		}

		return tooltips;
	}
}
