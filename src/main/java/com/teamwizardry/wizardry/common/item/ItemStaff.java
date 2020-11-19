package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.wizardry.api.StringConsts;
import com.teamwizardry.wizardry.api.item.INacreProduct;
import com.teamwizardry.wizardry.api.spell.Interactor;
import com.teamwizardry.wizardry.common.spell.SpellCompiler;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
	
	public boolean onEntitySwing(ItemStack stack, LivingEntity entity)
	{
	    // FIXME: Test Spell
	    Interactor caster = new Interactor(entity);
	    SpellCompiler.get()
	                 .compileSpell(new ItemStack(Items.BEEF), new ItemStack(Items.PORKCHOP))
	                 .toInstance(caster)
	                 .run(entity.getEntityWorld(), caster);
	    
	    return super.onEntitySwing(stack, entity);
	}
}
