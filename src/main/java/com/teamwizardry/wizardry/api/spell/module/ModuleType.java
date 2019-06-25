package com.teamwizardry.wizardry.api.spell.module;

/**
 * Created by Demoniaque.
 */
public enum ModuleType {

	EFFECT("effect"), SHAPE("shape"), EVENT("event"), MODIFIER("modifier");

	public String name;

	ModuleType(String name) {
		this.name = name;
	}
}
