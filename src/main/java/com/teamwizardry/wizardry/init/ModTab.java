package com.teamwizardry.wizardry.init;

import com.teamwizardry.librarianlib.features.base.ModCreativeTab;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque.
 */
public class ModTab extends ModCreativeTab {

	public ModTab() {
	}

	@Nonnull
	@Override
	public ItemStack getIconStack() {
		return new ItemStack(ModItems.BOOK);
	}
}
