package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.api.block.IStructure;
import com.teamwizardry.wizardry.api.block.WizardryStructureRenderCompanion;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

public class ModStructures {

	public static ModStructures INSTANCE = new ModStructures();

	private HashMap<ResourceLocation, WizardryStructureRenderCompanion> structures = new HashMap<>();

	private ModStructures() {
	}

	public WizardryStructureRenderCompanion getStructure(Block block) {
		if (!(block instanceof IStructure) || block.getRegistryName() == null) return null;

		structures.putIfAbsent(block.getRegistryName(), new WizardryStructureRenderCompanion(block.getRegistryName()));

		return structures.get(block.getRegistryName());
	}

	public WizardryStructureRenderCompanion getStructure(String blockName) {
		ResourceLocation loc = new ResourceLocation(blockName);

		structures.putIfAbsent(loc, new WizardryStructureRenderCompanion(loc));

		return structures.get(loc);
	}
}

