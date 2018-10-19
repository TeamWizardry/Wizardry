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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModuleShape extends Module {

	public ModuleShape(IModuleShape moduleClass, ItemStack itemStack, Color primaryColor, Color secondaryColor,
			DefaultHashMap<Attribute, AttributeRange> attributeRanges) {
		super(moduleClass, itemStack, primaryColor, secondaryColor, attributeRanges);
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


}
