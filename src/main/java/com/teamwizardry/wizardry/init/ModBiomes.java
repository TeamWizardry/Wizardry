package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.common.world.biome.BiomeTorikki;
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
    public static Biome BIOME_TORIKKI = new BiomeTorikki(new Biome.BiomeProperties("wizardry_torikki")).setRegistryName("wizardry_torikki");

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Biome> evt) {
        IForgeRegistry<Biome> r = evt.getRegistry();

        r.register(BIOME_UNDERWORLD);
        r.register(BIOME_TORIKKI);
        BiomeDictionary.addTypes(BIOME_TORIKKI, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.SPOOKY, BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(BIOME_UNDERWORLD, BiomeDictionary.Type.VOID, BiomeDictionary.Type.SPOOKY, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.DRY, BiomeDictionary.Type.COLD);
    }
}
