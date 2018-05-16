package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.wizardry.api.spell.module.ModuleShape;

public class OverrideObject {

	private final Class<? extends ModuleShape> clazz;
	private final OverrideConsumer<SpellData, SpellRing, SpellRing> consumer;

	public OverrideObject(Class<? extends ModuleShape> clazz, OverrideConsumer<SpellData, SpellRing, SpellRing> consumer) {
		this.clazz = clazz;
		this.consumer = consumer;
	}

	public Class<? extends ModuleShape> getModuleClass() {
		return clazz;
	}

	public OverrideConsumer<SpellData, SpellRing, SpellRing> getConsumer() {
		return consumer;
	}
	
	@FunctionalInterface
	public interface OverrideConsumer<T, U, V>
	{
		public void accept(T t, U u, V v);
	}
}
