package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.features.base.item.IItemColorProvider;
import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.api.Constants;
import kotlin.jvm.functions.Function2;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Created by Saad on 8/28/2016.
 */
public class ItemFairyWings extends ItemMod implements IItemColorProvider {

	public ItemFairyWings() {
		super("fairy_wings");
		setMaxStackSize(16);
	}


	@Nullable
	@Override
	public Function2<ItemStack, Integer, Integer> getItemColorFunction() {
		return (stack, tintIndex) -> ItemNBTHelper.getInt(stack, Constants.NBT.FAIRY_COLOR, 0xFFFFFF);
	}
}
