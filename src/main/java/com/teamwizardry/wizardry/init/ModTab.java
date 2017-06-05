package com.teamwizardry.wizardry.init;

import com.teamwizardry.librarianlib.features.base.ModCreativeTab;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Created by LordSaad.
 */
public class ModTab extends ModCreativeTab {

	public ModTab() {
	}

	@NotNull
	@Override
	public ItemStack getIconStack() {
		return new ItemStack(ModItems.BOOK);
	}
}
