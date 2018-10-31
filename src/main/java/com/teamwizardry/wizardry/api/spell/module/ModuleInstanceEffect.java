package com.teamwizardry.wizardry.api.spell.module;

import java.awt.Color;
import javax.annotation.Nonnull;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRange;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry.Attribute;
import com.teamwizardry.wizardry.api.util.DefaultHashMap;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModuleInstanceEffect extends ModuleInstance {

	public ModuleInstanceEffect(IModuleEffect moduleClass, ModuleFactory createdByFactory, String subModuleID, ResourceLocation icon, ItemStack itemStack, Color primaryColor, Color secondaryColor,
			DefaultHashMap<Attribute, AttributeRange> attributeRanges) {
		super(moduleClass, createdByFactory, subModuleID, icon, itemStack, primaryColor, secondaryColor, attributeRanges);
		moduleClass.initEffect(this);
	}
	
	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	/**
	 * Only return false if the spellData cannot be taxed from mana. Return true otherwise.
	 */
	@Override
	public boolean run(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		return ((IModuleEffect)moduleClass).run(this, spell, spellRing);
	}
	
	/**
	 * Will render whatever GL code is specified here while the spell is being held by the
	 * player's hand.
	 */
	@Override
	@Nonnull
	@SideOnly(Side.CLIENT)
	public SpellData renderVisualization(@Nonnull SpellData data, @Nonnull SpellRing ring, @Nonnull SpellData previousData) {
		return ((IModuleEffect)moduleClass).renderVisualization(this, data, ring, previousData);
	}
	
	/**
	 * This method runs client side when the spellData runs. Spawn particles here.
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		((IModuleEffect)moduleClass).renderSpell(this, spell, spellRing);
	}
}
