package com.teamwizardry.wizardry.api.spell.module;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRange;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry.Attribute;
import com.teamwizardry.wizardry.api.util.DefaultHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.awt.*;

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
	 * {@inheritDoc}
	 */
	@Override
	public boolean ignoreResultsForRendering() {
		return ((IModuleEffect)moduleClass).ignoreResultsForRendering();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean shouldRunChildren() {
		return ((IModuleEffect)moduleClass).shouldRunChildren();
	}

	/**
	 * Only return false if the spellData cannot be taxed from mana. Return true otherwise.
	 */
	@Override
	public boolean run(@Nonnull World world, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		return ((IModuleEffect) moduleClass).run(world, this, spell, spellRing);
	}
	
	/**
	 * Will render whatever GL code is specified here while the spell is being held by the
	 * player's hand.
	 */
	@Override
	@Nonnull
	@SideOnly(Side.CLIENT)
	public SpellData renderVisualization(@Nonnull World world, @Nonnull SpellData data, @Nonnull SpellRing ring, float partialTicks) {
		return ((IModuleEffect) moduleClass).renderVisualization(world, this, data, ring, partialTicks);
	}
	
	/**
	 * This method runs client side when the spellData runs. Spawn particles here.
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(@Nonnull World world, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		((IModuleEffect) moduleClass).renderSpell(world, this, spell, spellRing);
	}
}
