package com.teamwizardry.wizardry.data;

import javax.annotation.Nonnull;

import com.teamwizardry.wizardry.Wizardry;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockModelsProvider extends BlockStateProvider {


	public BlockModelsProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
		super(gen, Wizardry.MODID, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {

	}

	@Nonnull
	@Override
	public String getName() {
		return "Wizardry blockstates and block models";
	}
}
