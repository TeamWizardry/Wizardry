package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.common.world.biome.BiomeUnderWorld;
import net.minecraft.world.biome.Biome;

/**
 * Created by LordSaad.
 */
public class ModBiomes {

	public static void init() {
		Biome.registerBiome(42, "wizardry_underworld", new BiomeUnderWorld(new Biome.BiomeProperties("wizardry_underworld")));
	}
}
