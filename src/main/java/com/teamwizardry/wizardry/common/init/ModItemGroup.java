package com.teamwizardry.wizardry.common.init;

import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModItemGroup extends ItemGroup {
	public static final ModItemGroup INSTANCE = new ModItemGroup();


	public ModItemGroup() {
		super(Wizardry.MODID);
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack(ItemInit.wisdomStick);
	}

	@Override
	public boolean hasSearchBar() {
		return true;
	}
}
