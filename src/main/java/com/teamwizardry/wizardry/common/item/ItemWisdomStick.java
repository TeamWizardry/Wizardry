package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import net.minecraftforge.oredict.OreDictionary;

public class ItemWisdomStick extends ItemMod {

	public ItemWisdomStick() {
		super("wisdom_stick");
		OreDictionary.registerOre("stickWood", this);
	}
}
