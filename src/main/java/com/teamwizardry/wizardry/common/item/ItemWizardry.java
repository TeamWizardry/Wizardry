package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import com.teamwizardry.librarianlib.common.base.item.ItemMod;
import com.teamwizardry.wizardry.Wizardry;
import org.jetbrains.annotations.Nullable;

/**
 * @author WireSegal
 *         Created at 2:13 PM on 8/26/16.
 */
public abstract class ItemWizardry extends ItemMod {

	public ItemWizardry(String name, String... variants) {
		super(name, variants);
	}

	@Nullable
	@Override
	public ModCreativeTab getCreativeTab() {
		return Wizardry.tab;
	}
}
