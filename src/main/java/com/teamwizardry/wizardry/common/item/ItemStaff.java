package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.wizardry.api.StringConsts;
import com.teamwizardry.wizardry.api.item.INacreProduct;
import com.teamwizardry.wizardry.api.spell.Interactor;
import com.teamwizardry.wizardry.common.spell.SpellCompiler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

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

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {

		Interactor caster = new Interactor(player);
		SpellCompiler.get()
				.compileSpell(new ItemStack(Items.BEEF), new ItemStack(Items.PORKCHOP))
				.toInstance(caster)
				.run(world, caster);

		player.setActiveHand(hand);

		return super.onItemRightClick(world, player, hand);
	}
}
