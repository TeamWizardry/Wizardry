package com.teamwizardry.wizardry.api.events;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.module.Module;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by Demoniaque.
 */
public class SpellCastEvent extends Event {

	public Module module;
	public SpellData spell;
	public boolean castParticles = true;

	public SpellCastEvent(Module module, SpellData spell) {
		this.module = module;
		this.spell = spell;
	}
}
