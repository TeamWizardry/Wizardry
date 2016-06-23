package me.lordsaad.wizardry.spells.modules.modifiers;

import me.lordsaad.wizardry.api.modules.Module;
import me.lordsaad.wizardry.api.modules.attribute.AttributeMap;

public abstract class ModuleModifier extends Module {
	
	public abstract void apply(AttributeMap map);

}
