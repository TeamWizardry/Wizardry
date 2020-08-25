package com.teamwizardry.wizardry.data;

import com.teamwizardry.wizardry.common.init.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ForgeItemTagsProvider;

public class ItemTagProvider extends ForgeItemTagsProvider {
	public ItemTagProvider(DataGenerator gen) {
		super(gen);
	}

	@Override
	public void registerTags() {
		getBuilder(Tags.Items.RODS_WOODEN).add(ModItems.wisdomStick.get());
	}

	@Override
	public String getName() {
		return "Wizardry item tags";
	}
}
