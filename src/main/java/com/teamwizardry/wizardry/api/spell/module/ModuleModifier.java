package com.teamwizardry.wizardry.api.spell.module;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRange;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry.Attribute;
import com.teamwizardry.wizardry.api.util.DefaultHashMap;

import net.minecraft.item.ItemStack;

import java.awt.Color;

import javax.annotation.Nonnull;

public class ModuleModifier extends Module {

	public ModuleModifier(IModuleModifier moduleClass, String moduleName, ItemStack itemStack, Color primaryColor, Color secondaryColor,
			DefaultHashMap<Attribute, AttributeRange> attributeRanges) {
		super(moduleClass, moduleName, itemStack, primaryColor, secondaryColor, attributeRanges);
	}

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.MODIFIER;
	}

	public String getShortHandName() {
		return LibrarianLib.PROXY.translate(getShortHandKey());
	}

	public String getShortHandKey() {
		return "wizardry.spell." + moduleName + ".short";
	}

}
