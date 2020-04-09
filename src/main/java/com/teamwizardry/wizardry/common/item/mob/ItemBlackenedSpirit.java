package com.teamwizardry.wizardry.common.item.mob;

import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Demoniaque on 6/21/2016.
 */
public class ItemBlackenedSpirit extends ItemMod {

	public ItemBlackenedSpirit() {
		super("blackened_spirit");
	}

	@NotNull
	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.UNCOMMON;
	}
}
