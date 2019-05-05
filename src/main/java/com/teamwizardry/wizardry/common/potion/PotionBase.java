package com.teamwizardry.wizardry.common.potion;

import com.teamwizardry.librarianlib.features.base.PotionMod;
import com.teamwizardry.librarianlib.features.helpers.VariantHelper;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class PotionBase extends PotionMod {

	public PotionBase(@Nonnull String name, boolean badEffect, int color) {
		super(name, badEffect, color);
		setPotionName("potion." + Wizardry.MODID + "." + VariantHelper.toSnakeCase(name));
	}

	@NotNull
	@Override
	public List<ItemStack> getCurativeItems() {
		return new ArrayList<>();
	}
}
