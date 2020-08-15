package com.teamwizardry.wizardry.api.spell;

public interface ITargetComponent<T> extends ISpellComponent {

	boolean apply(T t);
}
