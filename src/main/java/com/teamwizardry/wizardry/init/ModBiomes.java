package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.common.world.biome.BiomeUnderWorld;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Created by LordSaad.
 */
@Mod.EventBusSubscriber
public class ModBiomes {

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Biome> evt) {
		IForgeRegistry<Biome> r = evt.getRegistry();
		r.register(new BiomeUnderWorld(new Biome.BiomeProperties("wizardry_underworld")).setRegistryName("wizardry_underworld"));
	}
}
