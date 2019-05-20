package com.teamwizardry.wizardry.api.spell.module;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRange;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry.Attribute;
import com.teamwizardry.wizardry.api.util.DefaultHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.awt.*;

public class ModuleInstanceEvent extends ModuleInstance {
	
	public ModuleInstanceEvent(IModuleEvent moduleClass, ModuleFactory createdByFactory, ItemStack itemStack, String subModuleID, ResourceLocation icon, Color primaryColor, Color secondaryColor,
			DefaultHashMap<Attribute, AttributeRange> attributeRanges) {
		super(moduleClass, createdByFactory, subModuleID, icon, itemStack, primaryColor, secondaryColor, attributeRanges);
	}

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EVENT;
	}
	
	/**
	 * {@inheritDoc}	
	 */
	@Override
	public boolean shouldRunChildren() {
		return ((IModuleEvent)moduleClass).shouldRunChildren();
	}
	
	/**
	 * Only return false if the spellData cannot be taxed from mana. Return true otherwise.
	 */
	@Override
	public boolean run(@Nonnull World world, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		return ((IModuleEvent) moduleClass).run(world, this, spell, spellRing);
	}
}
