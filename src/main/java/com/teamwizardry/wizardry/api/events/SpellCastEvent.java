package com.teamwizardry.wizardry.api.events;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by Demoniaque.
 */
public class SpellCastEvent extends Event {

	private final World world;
	private SpellRing spellRing;
	private SpellData spellData;

	public SpellCastEvent(World world, SpellRing spellRing, SpellData spellData) {
		this.world = world;
		this.spellRing = spellRing;
		this.spellData = spellData;
	}


	public SpellRing getSpellRing() {
		return spellRing;
	}

	public void setSpellRing(SpellRing spellRing) {
		this.spellRing = spellRing;
	}

	public SpellData getSpellData() {
		return spellData;
	}

	public void setSpellData(SpellData spellData) {
		this.spellData = spellData;
	}

	public World getWorld() {
		return world;
	}
}
