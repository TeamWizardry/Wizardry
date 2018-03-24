package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.features.base.item.IItemColorProvider;
import com.teamwizardry.librarianlib.features.base.item.ItemModFood;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.api.Constants;
import kotlin.jvm.functions.Function2;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Created by Demoniaque on 8/28/2016.
 */
public class ItemFairyImbuedApple extends ItemModFood implements IItemColorProvider {

	public ItemFairyImbuedApple() {
		super("fairy_imbued_apple", 10, 1, false);
		setMaxStackSize(64);
	}

	@Nullable
	@Override
	public Function2<ItemStack, Integer, Integer> getItemColorFunction() {
		return (stack, tintIndex) -> ItemNBTHelper.getInt(stack, Constants.NBT.FAIRY_COLOR, 0xFFFFFF);
	}
}
