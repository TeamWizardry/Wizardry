package com.teamwizardry.wizardry.api.spell.module;

import java.awt.Color;

import javax.annotation.Nonnull;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRange;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry.Attribute;
import com.teamwizardry.wizardry.api.util.DefaultHashMap;

import kotlin.Pair;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModuleInstanceShape extends ModuleInstance {

	public ModuleInstanceShape(IModuleShape moduleClass, ModuleFactory createdByFactory, ItemStack itemStack, String moduleName, ResourceLocation icon, Color primaryColor, Color secondaryColor,
			DefaultHashMap<Attribute, AttributeRange> attributeRanges) {
		super(moduleClass, createdByFactory, moduleName, icon, itemStack, primaryColor, secondaryColor, attributeRanges);
	}

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.SHAPE;
	}

	public boolean runRunOverrides(SpellData data, SpellRing ring) {
		boolean overriden = false;
		for (SpellRing child : ring.getAllChildRings()) {
			if (child.getModule().getModuleType() == ModuleType.SHAPE) break;
			OverrideConsumer<SpellData, SpellRing, SpellRing> consumer = ModuleRegistry.INSTANCE.runOverrides.get(new Pair<>(this, child.getModule()));
			if (consumer == null) continue;

			consumer.accept(data, ring, child);
			overriden = true;
		}
		return overriden;
	}

	@SideOnly(Side.CLIENT)
	public boolean runRenderOverrides(SpellData data, SpellRing ring) {
		boolean overriden = false;
		for (SpellRing child : ring.getAllChildRings()) {
			if (child.getModule().getModuleType() == ModuleType.SHAPE) break;
			OverrideConsumer<SpellData, SpellRing, SpellRing> consumer = ModuleRegistry.INSTANCE.renderOverrides.get(new Pair<>(this, child.getModule()));
			if (consumer == null) continue;

			consumer.accept(data, ring, child);
			overriden = true;
		}
		return overriden;
	}
	
	/**
	 * Only return false if the spellData cannot be taxed from mana. Return true otherwise.
	 */
	@Override
	public boolean run(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		return ((IModuleShape)moduleClass).run(this, spell, spellRing);
	}

	/**
	 * Will render whatever GL code is specified here while the spell is being held by the
	 * player's hand.
	 */
	@Override
	@Nonnull
	@SideOnly(Side.CLIENT)
	public SpellData renderVisualization(@Nonnull SpellData data, @Nonnull SpellRing ring, @Nonnull SpellData previousData) {
		return ((IModuleShape)moduleClass).renderVisualization(this, data, ring, previousData);
	}
	
	/**
	 * This method runs client side when the spellData runs. Spawn particles here.
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		((IModuleShape)moduleClass).renderSpell(this, spell, spellRing);
	}
}
