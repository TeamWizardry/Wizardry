package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.core.common.OreDictionaryRegistrar;
import com.teamwizardry.librarianlib.features.base.item.ItemMod;

public class ItemWisdomStick extends ItemMod {

	public ItemWisdomStick() {
		super("wisdom_stick");
		OreDictionaryRegistrar.registerOre("stickWood", this);
	}
}
