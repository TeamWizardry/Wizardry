package com.teamwizardry.wizardry.common.potion;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Demoniaque.
 */
public class PotionBouncing extends PotionBase {

	public PotionBouncing() {
		super("bouncing", false, 0xABFCF0);
	}

	@Nonnull
	@Override
	public List<ItemStack> getCurativeItems() {
		return new ArrayList<>();
	}
}
