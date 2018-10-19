package com.teamwizardry.wizardry.api.spell.module;

import java.awt.Color;
import java.util.HashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRange;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry.Attribute;
import com.teamwizardry.wizardry.api.util.DefaultHashMap;

import kotlin.Pair;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModuleEffect extends Module {

	protected HashMap<String, OverrideConsumer<SpellData, SpellRing, SpellRing>> runOverrides = new HashMap<>();
	protected HashMap<String, OverrideConsumer<SpellData, SpellRing, SpellRing>> renderOverrides = new HashMap<>();

	public ModuleEffect(IModuleEffect moduleClass, ItemStack itemStack, Color primaryColor, Color secondaryColor,
			DefaultHashMap<Attribute, AttributeRange> attributeRanges) {
		super(moduleClass, itemStack, primaryColor, secondaryColor, attributeRanges);
		moduleClass.initEffect(this);
	}
	
	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	public void registerOverrides()
	{}
	
	public void registerRunOverride(String moduleID, OverrideConsumer<SpellData, SpellRing, SpellRing> runOverride)
	{
		runOverrides.put(moduleID, runOverride);
	}
	
	public void registerRenderOverride(String moduleID, OverrideConsumer<SpellData, SpellRing, SpellRing> renderOverride)
	{
		renderOverrides.put(moduleID, renderOverride);
	}

	@SideOnly(Side.CLIENT)
	public OverrideConsumer<SpellData, SpellRing, SpellRing> registerRenderOverride(String moduleID)
	{
		return null;
	}

	public boolean hasOverridingRuns(SpellRing spellRing) {
		SpellRing ring = spellRing.getParentRing();

		while (ring != null) {
			if (hasOverridingRuns(ring)) return true;

			ring = ring.getParentRing();
		}

		return false;
	}

	@SideOnly(Side.CLIENT)
	public boolean hasOverridingRenders(SpellRing spellRing) {
		SpellRing ring = spellRing.getParentRing();

		while (ring != null) {
			if (hasOverridingRenders(ring)) return true;

			ring = ring.getParentRing();
		}

		return false;
	}

	public boolean hasRunOverrideFor(Module module) {
		return ModuleRegistry.INSTANCE.runOverrides.containsKey(new Pair<>(module, this));
	}

	@SideOnly(Side.CLIENT)
	public boolean hasRenderOverrideFor(Module module) {
		return ModuleRegistry.INSTANCE.renderOverrides.containsKey(new Pair<>(module, this));
	}

	@Nullable
	public OverrideConsumer<SpellData, SpellRing, SpellRing> getRunOverrideFor(Module module) {
		return ModuleRegistry.INSTANCE.runOverrides.get(new Pair<>(module, this));
	}

	@Nullable
	@SideOnly(Side.CLIENT)
	public OverrideConsumer<SpellData, SpellRing, SpellRing> getRenderOverrideFor(Module module) {
		return ModuleRegistry.INSTANCE.renderOverrides.get(new Pair<>(module, this));
	}
}
