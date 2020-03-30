package com.teamwizardry.wizardry.data;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;

public class BlockTagProvider extends BlockTagsProvider {
	public BlockTagProvider(DataGenerator generatorIn) {
		super(generatorIn);
	}

	@Override
	protected void registerTags() {
	}

	@Override
	public String getName() {
		return "Wizardry block tags";
	}
}
