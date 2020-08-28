package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.wizardry.api.StringConsts;
import com.teamwizardry.wizardry.api.item.INacreProduct;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

/**
 * Project: Wizardry
 * Created by Carbon
 * Copyright (c) Carbon 2020
 */
public class ItemStaff extends Item implements INacreProduct.INacreDecayProduct {
	public ItemStaff(Properties properties) {
		super(properties);

		this.addPropertyOverride(new ResourceLocation("staff_state"), (stack, world, entity) -> stack.hasTag() && stack.getTag().contains(StringConsts.SPELL_DATA) ? 1 : 0);
	}
}
