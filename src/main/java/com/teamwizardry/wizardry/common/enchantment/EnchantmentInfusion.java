package com.teamwizardry.wizardry.common.enchantment;

import com.teamwizardry.librarianlib.features.base.EnchantmentMod;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EnchantmentInfusion extends EnchantmentMod {

	public EnchantmentInfusion() {
		super("infusion", Rarity.COMMON, EnumEnchantmentType.ALL, EntityEquipmentSlot.MAINHAND);
	}
	
	@Override
    public boolean canApplyAtEnchantingTable(ItemStack stack)
    {
        return false;
    }
    
	@Override
    public boolean canApply(ItemStack stack)
    {
        return true;
    }
	
	@Override
	public String getTranslatedName(int level) {
		return super.getTranslatedName(level);
	}
}
