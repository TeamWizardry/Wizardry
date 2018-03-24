package com.teamwizardry.wizardry.api.spell.module;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;

import javax.annotation.Nonnull;

public abstract class ModuleModifier extends Module {

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.MODIFIER;
	}

	public String getShortHandName() {
		return LibrarianLib.PROXY.translate(getShortHandKey());
	}

	public String getShortHandKey() {
		return "wizardry.spell." + getID() + ".short";
	}

	@Override
	public boolean run(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		return true;
	}

	@Override
	public void render(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {

	}
}
