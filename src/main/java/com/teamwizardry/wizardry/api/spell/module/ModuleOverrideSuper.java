package com.teamwizardry.wizardry.api.spell.module;

import com.teamwizardry.wizardry.api.spell.module.ModuleOverrideHandler.OverridePointer;

public class ModuleOverrideSuper {
	private final OverridePointer pointer;
	
	ModuleOverrideSuper(OverridePointer pointer) {
		// NOTE: Is only created within ModuleOverrideHandler
		this.pointer = pointer;
	}
	
	public boolean hasSuper() {
		return pointer.getPrev() != null;
	}
}
