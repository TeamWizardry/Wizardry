package com.teamwizardry.wizardry.api.spell.module;

import com.teamwizardry.wizardry.api.spell.OverrideObject;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiConsumer;

public abstract class ModuleEffect extends Module {

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	public OverrideObject[] getRunOverrides() {
		return new OverrideObject[0];
	}

	@SideOnly(Side.CLIENT)
	public OverrideObject[] getRenderOverrides() {
		return new OverrideObject[0];
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
		for (OverrideObject overrideObject : getRunOverrides()) {
			if (module.getClass().isAssignableFrom(overrideObject.getModuleClass())) return true;
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	public boolean hasRenderOverrideFor(Module module) {
		for (OverrideObject overrideObject : getRenderOverrides()) {
			if (module.getClass().isAssignableFrom(overrideObject.getModuleClass())) return true;
		}
		return false;
	}

	@Nullable
	public BiConsumer<SpellData, SpellRing> getRunOverrideFor(Module module) {
		for (OverrideObject overrideObject : getRunOverrides()) {
			if (module.getClass().isAssignableFrom(overrideObject.getModuleClass()))
				return overrideObject.getConsumer();
		}
		return null;
	}

	@Nullable
	@SideOnly(Side.CLIENT)
	public BiConsumer<SpellData, SpellRing> getRenderOverrideFor(Module module) {
		for (OverrideObject overrideObject : getRenderOverrides()) {
			if (module.getClass().isAssignableFrom(overrideObject.getModuleClass()))
				return overrideObject.getConsumer();
		}
		return null;
	}
}
