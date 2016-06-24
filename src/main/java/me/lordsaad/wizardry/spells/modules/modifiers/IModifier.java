package me.lordsaad.wizardry.spells.modules.modifiers;

import me.lordsaad.wizardry.api.modules.attribute.AttributeMap;

public interface IModifier {
	
	/**
	 * Adds the attributes to the map
	 * @return whether the attributes were added.
	 */
	public void apply(AttributeMap map);
	
	/**
	 * Whether to fall back to normal module handling if any of the attributes aren't supported.
	 * 
	 * Useful for modules that act as their own modifier
	 */
	public default boolean doesFallback() {
		return false;
	}

}
