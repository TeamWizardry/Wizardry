package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.common.enchantment.EnchantmentInfusion;

public class ModEnchantments {
	public static EnchantmentInfusion enchantmentInfusion;
	
	public static void init() { 
		enchantmentInfusion = new EnchantmentInfusion();
	}
}
