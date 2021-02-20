package com.teamwizardry.wizardry.data;

import com.teamwizardry.wizardry.Wizardry;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = Wizardry.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		event.getGenerator().addProvider(new BlockLootProvider(event.getGenerator()));
		event.getGenerator().addProvider(new BlockModelsProvider(event.getGenerator(), event.getExistingFileHelper()));
		event.getGenerator().addProvider(new ItemModelsProvider(event.getGenerator(), event.getExistingFileHelper()));
		event.getGenerator().addProvider(new ItemTagProvider(event.getGenerator()));
		event.getGenerator().addProvider(new RecipesProvider(event.getGenerator()));

	}
}
