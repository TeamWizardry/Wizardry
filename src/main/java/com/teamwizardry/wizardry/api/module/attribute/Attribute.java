package com.teamwizardry.wizardry.api.module.attribute;

import com.teamwizardry.wizardry.api.Constants.Module;

public class Attribute {

	public static final Attribute
			POWER = new Attribute(Module.POWER),
			DURATION = new Attribute(Module.DURATION),
			RADIUS = new Attribute(Module.RADIUS),
			PIERCE = new Attribute(Module.PIERCE),
			SILENT = new Attribute(Module.SILENT),
			SPEED = new Attribute(Module.SPEED),
			KNOCKBACK = new Attribute(Module.KNOCKBACK),
			PROJ_COUNT = new Attribute(Module.PROJ_COUNT),
			SCATTER = new Attribute(Module.SCATTER),
			CRIT_CHANCE = new Attribute(Module.CRIT_CHANCE),
			CRIT_DAMAGE = new Attribute(Module.CRIT_DAMAGE),
			DISTANCE = new Attribute(Module.DISTANCE),
			DAMAGE = new Attribute(Module.DAMAGE),
			MANA = new Attribute("Mana"),
			BURNOUT = new Attribute(Module.BURNOUT);

	public final String name;

	public Attribute(String name) {
		this.name = name;
	}

}
