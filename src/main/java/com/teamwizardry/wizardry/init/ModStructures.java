package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.block.IStructure;
import com.teamwizardry.wizardry.api.block.WizardryStructure;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

public class ModStructures {

	public static ModStructures INSTANCE = new ModStructures();

	private HashMap<String, WizardryStructure> structures = new HashMap<>();

	private ModStructures() {
	}

	public WizardryStructure getStructure(Block block) {
		if (block instanceof IStructure && !structures.containsKey(block.getUnlocalizedName())) {
			structures.put(block.getUnlocalizedName(), new WizardryStructure(new ResourceLocation(Wizardry.MODID, block.getUnlocalizedName()), ((IStructure) block).offsetToCenter()));
		}

		return structures.get(block.getUnlocalizedName());
	}
}

