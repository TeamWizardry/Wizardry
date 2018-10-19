package com.teamwizardry.wizardry.api.spell.module;

import java.awt.Color;

import javax.annotation.Nonnull;

import com.teamwizardry.wizardry.api.spell.attribute.AttributeRange;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry.Attribute;
import com.teamwizardry.wizardry.api.util.DefaultHashMap;

import net.minecraft.item.ItemStack;

public class ModuleEvent extends Module {
	
	public ModuleEvent(IModuleEvent moduleClass, ItemStack itemStack, Color primaryColor, Color secondaryColor,
			DefaultHashMap<Attribute, AttributeRange> attributeRanges) {
		super(moduleClass, itemStack, primaryColor, secondaryColor, attributeRanges);
	}

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EVENT;
	}
}
