package com.teamwizardry.wizardry.common.module.events;

import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.RegisterModule;
import com.teamwizardry.wizardry.api.spell.SpellData;

import javax.annotation.Nonnull;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEventAlongPath extends Module {

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EVENT;
	}

	@Nonnull
	@Override
	public String getID() {
		return "event_along_path";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "While Along Path";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Triggered throughout the journey of a spell, like a projectile shape whilst airborne or across an entire beam shape";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		return true;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {

	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleEventAlongPath());
	}
}
