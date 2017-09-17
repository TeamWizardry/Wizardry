package com.teamwizardry.wizardry.common.world.biome;

import com.teamwizardry.wizardry.common.entity.EntityFairy;
import com.teamwizardry.wizardry.common.entity.EntitySpiritWight;
import com.teamwizardry.wizardry.common.entity.EntityUnicorn;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * Created by LordSaad44
 */
public class BiomeUnderWorld extends Biome {

	public BiomeUnderWorld(BiomeProperties properties) {
		super(properties);
		properties.setRainDisabled();

		this.topBlock = Blocks.AIR.getDefaultState();
		this.fillerBlock = Blocks.AIR.getDefaultState();
		spawnableCreatureList.clear();
		spawnableWaterCreatureList.clear();
		spawnableMonsterList.clear();
		spawnableCaveCreatureList.clear();
		modSpawnableLists.clear();
		this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityFairy.class, 100, 1, 5));
		this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityUnicorn.class, 10, 1, 3));
		this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntitySpiritWight.class, 20, 1, 1));
	}

	@Override
	public void decorate(@Nonnull World worldIn, @Nonnull Random rand, @Nonnull BlockPos pos) {

	}
}
