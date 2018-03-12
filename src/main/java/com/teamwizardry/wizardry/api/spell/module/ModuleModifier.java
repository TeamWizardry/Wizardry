package com.teamwizardry.wizardry.api.spell.module;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeModifier;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class ModuleModifier extends Module {

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.MODIFIER;
	}

	public void apply(@Nonnull List<AttributeModifier> attributeModifiers) {
		attributeModifiers.addAll(getAttributes());
	}

	public String getShortHandName() {
		return LibrarianLib.PROXY.translate(getShortHandKey());
	}

	public String getShortHandKey() {
		return "wizardry.spellData." + getID() + ".short";
	}

	@Override
	public boolean run(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		return true;
	}

	@Override
	public void render(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {

	}
}
