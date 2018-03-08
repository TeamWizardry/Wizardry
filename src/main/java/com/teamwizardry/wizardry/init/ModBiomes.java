package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.common.world.biome.BiomeUnderWorld;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Created by Demoniaque.
 */
@Mod.EventBusSubscriber
public class ModBiomes {

	public static Biome BIOME_UNDERWORLD = new BiomeUnderWorld(new Biome.BiomeProperties("wizardry_underworld")).setRegistryName("wizardry_underworld");

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Biome> evt) {
		IForgeRegistry<Biome> r = evt.getRegistry();

		r.register(BIOME_UNDERWORLD);

		BiomeDictionary.addTypes(BIOME_UNDERWORLD, BiomeDictionary.Type.VOID, BiomeDictionary.Type.SPOOKY, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.DRY, BiomeDictionary.Type.COLD);
	}
}
