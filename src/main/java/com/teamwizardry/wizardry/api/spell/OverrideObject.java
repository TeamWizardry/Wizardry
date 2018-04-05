package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.wizardry.api.spell.module.ModuleShape;

import java.util.function.BiConsumer;

public class OverrideObject {

	private final Class<? extends ModuleShape> clazz;
	private final BiConsumer<SpellData, SpellRing> consumer;

	public OverrideObject(Class<? extends ModuleShape> clazz, BiConsumer<SpellData, SpellRing> consumer) {
		this.clazz = clazz;
		this.consumer = consumer;
	}

	public Class<? extends ModuleShape> getModuleClass() {
		return clazz;
	}

	public BiConsumer<SpellData, SpellRing> getConsumer() {
		return consumer;
	}
}
