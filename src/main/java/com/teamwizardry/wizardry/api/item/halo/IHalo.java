package com.teamwizardry.wizardry.api.item.halo;

import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.common.item.halos.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public interface IHalo {

	default List<String> getHaloTooltip(ItemStack stack) {
		List<String> tooltips = new ArrayList<>();

		if(stack == null) {
			return tooltips;
		}

		Item halo = stack.getItem();

		if(halo instanceof ItemFakeHaloBauble || halo instanceof ItemFakeHaloHead) {
			tooltips.add(TextFormatting.YELLOW + "Maximum Mana: " + ConfigValues.crudeHaloBufferSize);
		} else if(halo instanceof ItemRealHaloBauble || halo instanceof ItemRealHaloHead) {
			tooltips.add(TextFormatting.YELLOW + "Maximum Mana: " + ConfigValues.realHaloBufferSize);
		} else if(halo instanceof ItemCreativeHaloBauble || halo instanceof ItemCreativeHaloHead) {
			tooltips.add(TextFormatting.YELLOW + "Maximum Mana: " + ConfigValues.creativeHaloBufferSize);
		} else {
			tooltips.add(TextFormatting.RED + "Something went wrong! This halo has no tooltip!");
		}

		/*
		// TO BE ADDED LATER
		NBTTagList slots = NBTHelper.getList(stack, "slots", NBTTagString.class);
		if (slots == null) {
			// tooltips.add(TextFormatting.GRAY + "<EMPTY>");  // TBA
			return tooltips;
		}

		for (int j = 0; j < slots.tagCount(); j++) {
			String string = slots.getStringTagAt(j);
			HaloInfusionItem infusionItem = HaloInfusionItemRegistry.getItemFromName(string);
			if (infusionItem != HaloInfusionItemRegistry.EMPTY) {
				tooltips.add(TextFormatting.GOLD + "- " + infusionItem.getStack().getDisplayName());
			}
		}
		*/

		return tooltips;
	}
}
