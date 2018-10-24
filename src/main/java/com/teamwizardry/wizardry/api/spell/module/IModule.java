package com.teamwizardry.wizardry.api.spell.module;

import javax.annotation.Nullable;

public interface IModule {

	/**
	 * Specify all applicable modifiers that can be applied to this module.
	 *
	 * @return Any set with applicable ModuleModifiers.
	 */
	@Nullable
	default String[] compatibleModifierClasses() {
		return null;
	}
	
	default boolean ignoreResultForRendering() {
		return false;
	}
}
