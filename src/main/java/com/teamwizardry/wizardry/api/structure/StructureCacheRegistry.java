package com.teamwizardry.wizardry.api.structure;

import java.util.HashSet;
import java.util.Set;

public class StructureCacheRegistry {

	public static StructureCacheRegistry INSTANCE = new StructureCacheRegistry();

	private Set<CachedStructure> structures = new HashSet<>();

	private StructureCacheRegistry() {
	}

	public CachedStructure addStructure(String name) {
		CachedStructure structure = new CachedStructure(name, null);
		if (structure.blockInfos().isEmpty()) return null;
		structures.add(structure);

		return structure;
	}

	public CachedStructure getStructureOrAdd(String name) {
		for (CachedStructure structure : structures) {
			if (structure.name.equals(name)) return structure;
		}

		return addStructure(name);
	}
}
