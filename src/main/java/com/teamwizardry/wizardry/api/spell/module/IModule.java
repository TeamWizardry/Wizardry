package com.teamwizardry.wizardry.api.spell.module;

import javax.annotation.Nullable;

public interface IModule {

	// Maybe remove from interface? Only declared in Module
//	List<String> getDetailedInfo();
	
	/**
	 * Specify all applicable modifiers that can be applied to this module.
	 *
	 * @return Any set with applicable ModuleModifiers.
	 */
	@Nullable
	default IModuleModifier[] applicableModifiers() {
		return null;
	}
	
	default boolean ignoreResultForRendering() {
		return false;
	}

	// Maybe remove from interface? Only declared in Module
//	List<AttributeModifier> getAttributes();

	// Maybe remove from interface? Only declared in Module
//	Map<Attribute, AttributeRange> getAttributeRanges();

	/**
	 * A lower case snake_case string id that reflects the module to identify it during serialization/deserialization.
	 *
	 * @return A lower case snake_case string.
	 */
	String getID();
	
}
