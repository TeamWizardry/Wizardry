package com.teamwizardry.wizardry.api.capability.player.mana;

import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.librarianlib.features.base.block.tile.module.ModuleCapability;
import org.jetbrains.annotations.NotNull;

/**
 * @author WireSegal
 * Created at 12:20 PM on 3/24/18.
 */
public class ManaModule extends ModuleCapability<CustomManaCapability> {

	public ManaModule(CustomManaCapability capability) {
		super(ManaCapabilityProvider.MANA_CAPABILITY, capability);
	}

	@Override
	public boolean hasComparatorOutput() {
		return true;
	}

	@Override
	public float getComparatorOutput(@NotNull TileMod tile) {
		return (float) (getHandler().getMana() / getHandler().getMaxMana());
	}
}
