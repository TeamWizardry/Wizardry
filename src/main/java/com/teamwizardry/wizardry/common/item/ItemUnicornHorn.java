package com.teamwizardry.wizardry.common.item;


import com.teamwizardry.librarianlib.features.base.ModCreativeTab;
import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.wizardry.Wizardry;

import javax.annotation.Nullable;

/**
 * Created by Saad on 8/28/2016.
 */
public class ItemUnicornHorn extends ItemMod {

	public ItemUnicornHorn() {
		super("unicorn_horn");
		setMaxStackSize(64);
	}

	@Nullable
	@Override
	public ModCreativeTab getCreativeTab() {
		return Wizardry.tab;
	}
}
