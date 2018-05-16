package com.teamwizardry.wizardry.api.spell.module;

import com.teamwizardry.wizardry.api.spell.OverrideObject.OverrideConsumer;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public abstract class ModuleShape extends Module {

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.SHAPE;
	}

	public boolean runRunOverrides(SpellData data, SpellRing ring) {
		boolean overriden = false;
		for (SpellRing child : ring.getAllChildRings()) {
			if (child.getModule() == null) continue;

			OverrideConsumer<SpellData, SpellRing, SpellRing> consumer = ring.getRunOverrideFrom(child.getModule());
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
			if (child.getModule() == null) continue;

			OverrideConsumer<SpellData, SpellRing, SpellRing> consumer = ring.getRenderOverrideFrom(child.getModule());
			if (consumer == null) continue;

			consumer.accept(data, ring, child);
			overriden = true;
		}

		return overriden;
	}


}
