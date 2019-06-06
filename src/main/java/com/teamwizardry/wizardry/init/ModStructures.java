package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.structure.StructureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public final class ModStructures {

	public static final ResourceLocation CRAFTING_PLATE = new ResourceLocation(Wizardry.MODID, "crafting_plate");
	public static final ResourceLocation MANA_BATTERY = new ResourceLocation(Wizardry.MODID, "mana_battery");
	public static StructureManager structureManager;

	private ModStructures() {
	}

	public static void init() {
		structureManager = new StructureManager();
		structureManager.addStructure(CRAFTING_PLATE, new BlockPos(4, 1, 4));
		structureManager.addStructure(MANA_BATTERY, new BlockPos(5, 4, 5));
	}
}

