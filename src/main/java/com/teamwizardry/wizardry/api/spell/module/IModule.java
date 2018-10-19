package com.teamwizardry.wizardry.api.spell.module;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;

public interface IModule<InstanceType extends Module> {

	/**
	 * Will render whatever GL code is specified here while the spell is being held by the
	 * player's hand.
	 */
	default SpellData renderVisualization(InstanceType instance, @Nonnull SpellData data, @Nonnull SpellRing ring, @Nonnull SpellData previousData) {
		return new SpellData(data.world);
	}

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
	
	/**
	 * Only return false if the spellData cannot be taxed from mana. Return true otherwise.
	 */
	boolean run(InstanceType instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing);

	/**
	 * This method runs client side when the spellData runs. Spawn particles here.
	 */
	default void renderSpell(InstanceType instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		
	}

}
