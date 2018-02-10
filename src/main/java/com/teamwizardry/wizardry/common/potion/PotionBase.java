package com.teamwizardry.wizardry.common.potion;

import com.teamwizardry.librarianlib.features.base.PotionMod;
import com.teamwizardry.librarianlib.features.helpers.VariantHelper;
import com.teamwizardry.wizardry.Wizardry;

import javax.annotation.Nonnull;

public class PotionBase extends PotionMod {

	public PotionBase(@Nonnull String name, boolean badEffect, int color) {
		super(name, badEffect, color);
		setPotionName("potion." + Wizardry.MODID + "." + VariantHelper.toSnakeCase(name));
	}
}
