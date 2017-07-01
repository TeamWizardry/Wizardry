package com.teamwizardry.wizardry.common.module.shapes;

import javax.annotation.Nonnull;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleShape;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;

import net.minecraft.entity.Entity;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleShapeSelf extends ModuleShape {

	@Nonnull
	@Override
	public String getID() {
		return "shape_self";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Self";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will run the spell on the caster";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		Entity caster = spell.getData(DefaultKeys.CASTER);
		if (caster == null) return false;

		return runNextModule(spell);
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {

	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleShapeSelf());
	}
}
