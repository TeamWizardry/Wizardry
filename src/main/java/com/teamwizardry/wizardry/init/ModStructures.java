package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.block.CachedStructure;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

public class ModStructures {

	public static ModStructures INSTANCE = new ModStructures();

	public HashMap<String, CachedStructure> structures = new HashMap<>();

	public ModStructures() {
		init();
	}

	public void init() {
		structures.clear();
		structures.put("crafting_altar", new CachedStructure(new ResourceLocation(Wizardry.MODID, "crafting_altar")).setBlock(ModBlocks.CRAFTING_PLATE));
		structures.put("mana_battery", new CachedStructure(new ResourceLocation(Wizardry.MODID, "mana_battery")).setBlock(ModBlocks.MANA_BATTERY));
	}
}

