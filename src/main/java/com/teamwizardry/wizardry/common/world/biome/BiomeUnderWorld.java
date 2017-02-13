package com.teamwizardry.wizardry.common.world.biome;

import com.teamwizardry.wizardry.common.entity.EntityFairy;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.NotNull;

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
		this.theBiomeDecorator.treesPerChunk = 0;
		this.theBiomeDecorator.flowersPerChunk = 0;
		this.theBiomeDecorator.bigMushroomsPerChunk = 0;
		this.theBiomeDecorator.clayPerChunk = 0;
		this.theBiomeDecorator.deadBushPerChunk = 0;
		this.theBiomeDecorator.mushroomsPerChunk = 0;
		this.theBiomeDecorator.reedsPerChunk = 0;
		this.theBiomeDecorator.sandPerChunk = 0;
		this.theBiomeDecorator.sandPerChunk2 = 0;
		this.theBiomeDecorator.treesPerChunk = 0;
		this.theBiomeDecorator.waterlilyPerChunk = 0;
		this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityFairy.class, 100, 1, 3));
	}

	@Override
	public void decorate(@NotNull World worldIn, @NotNull Random rand, @NotNull BlockPos pos) {

	}
}
