package com.teamwizardry.wizardry.api.spell.module;

import javax.annotation.Nullable;

public interface IModule {

	/**
	 * Specify all applicable modifiers that can be applied to this module.
	 *
	 * @return Any set with applicable ModuleModifiers.
	 */
	@Nullable
	default IModuleModifier[] applicableModifiers() {
		// TODO: Use string array instead. Later use parameter mapping with asterisks
		return null;
	}
	
	default boolean ignoreResultForRendering() {
		return false;
	}

	/**
	 * A lower case snake_case string id that reflects the module to identify it during serialization/deserialization.
	 *
	 * @return A lower case snake_case string.
	 */
	String getClassID();
}
