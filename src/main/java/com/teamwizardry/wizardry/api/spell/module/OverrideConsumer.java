package com.teamwizardry.wizardry.api.spell.module;

@FunctionalInterface
public interface OverrideConsumer<T, U, V>
{
	public void accept(T t, U u, V v);
}
