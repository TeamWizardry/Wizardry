package com.teamwizardry.wizardry.api.spell.module;

import javax.annotation.Nonnull;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;

public interface IModuleModifier extends IModule<ModuleModifier> {
	
	@Override
	default boolean run(ModuleModifier instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		return true;
	}
}
