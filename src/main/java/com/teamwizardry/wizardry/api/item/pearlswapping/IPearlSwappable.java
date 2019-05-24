package com.teamwizardry.wizardry.api.item.pearlswapping;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Implement this for any item that can interact with pearl belts or similar.
 * Will enable the pearl selection UI.
 */
public interface IPearlSwappable {

	/**
	 * @return The old pearl.
	 */
	@Nonnull
	ItemStack swapPearl(ItemStack pearlHolder, ItemStack stackSwipeTo);

}
