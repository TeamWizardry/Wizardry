package me.lordsaad.wizardry.spells.modules.modifiers;

import me.lordsaad.wizardry.api.modules.attribute.AttributeMap;

public interface IModifier {

    /**
     * Adds the attributes to the map
     *
     * @return whether the attributes were added.
     */
    void apply(AttributeMap map);

    /**
     * Whether to fall back to normal module handling if any of the attributes aren't supported.
     * <p>
     * Useful for modules that act as their own modifier
     */
    default boolean doesFallback() {
        return false;
    }

}
