package com.teamwizardry.wizardry.api.spell.module;

import com.teamwizardry.librarianlib.core.LibrarianLib;

import javax.annotation.Nonnull;

public abstract class ModuleModifier extends Module {
	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.MODIFIER;
	}

	public void apply(@Nonnull Module module)
	{
		module.modifiersToApply.addAll(modifiers);
	}

	public String getShortHandName() {
		return LibrarianLib.PROXY.translate(getShortHandKey());
	}

	public String getShortHandKey() {
		return "spell.wizardry." + getID() + ".short";
	}
}
