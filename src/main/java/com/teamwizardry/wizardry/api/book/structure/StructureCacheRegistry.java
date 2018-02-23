package com.teamwizardry.wizardry.api.book.structure;

import net.minecraft.util.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public class StructureCacheRegistry {

	private static Set<CachedStructure> structures = new HashSet<>();

	public static CachedStructure addStructure(String name) {
		CachedStructure structure = new CachedStructure(new ResourceLocation(name), null);
		if (structure.blockInfos().isEmpty()) return null;
		structures.add(structure);

		return structure;
	}

	public static CachedStructure getStructureOrAdd(String name) {
		ResourceLocation trueName = new ResourceLocation(name);
		for (CachedStructure structure : structures) {
			if (structure.name.equals(trueName)) return structure;
		}

		return addStructure(name);
	}
}
