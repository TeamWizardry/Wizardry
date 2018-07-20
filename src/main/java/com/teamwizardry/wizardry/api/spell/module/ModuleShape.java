package com.teamwizardry.wizardry.api.spell.module;

import javax.annotation.Nonnull;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;

import kotlin.Pair;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ModuleShape extends Module {

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
