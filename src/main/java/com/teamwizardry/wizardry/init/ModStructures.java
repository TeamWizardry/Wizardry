package com.teamwizardry.wizardry.init;

import com.teamwizardry.librarianlib.features.structure.Structure;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

public class ModStructures {

	public static ModStructures INSTANCE = new ModStructures();

	public HashMap<String, Structure> structures = new HashMap<>();

	public ModStructures() {
		init();
	}

	public void init() {
		structures.clear();
		structures.put("crafting_altar", new Structure(new ResourceLocation(Wizardry.MODID, "crafting_altar")));
		structures.put("mana_battery", new Structure(new ResourceLocation(Wizardry.MODID, "mana_battery")));
	}
}

