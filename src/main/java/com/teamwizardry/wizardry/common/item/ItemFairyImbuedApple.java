package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.features.base.ModCreativeTab;
import com.teamwizardry.librarianlib.features.base.item.IItemColorProvider;
import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import kotlin.jvm.functions.Function2;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Created by Saad on 8/28/2016.
 */
public class ItemFairyImbuedApple extends ItemMod implements IItemColorProvider {

	public ItemFairyImbuedApple() {
		super("fairy_imbued_apple");
		setMaxStackSize(64);
	}

	@Nullable
	@Override
	public Function2<ItemStack, Integer, Integer> getItemColorFunction() {
		return (stack, tintIndex) -> ItemNBTHelper.getInt(stack, Constants.NBT.FAIRY_COLOR, 0xFFFFFF);
	}

	@Nullable
	@Override
	public ModCreativeTab getCreativeTab() {
		return Wizardry.tab;
	}
}
