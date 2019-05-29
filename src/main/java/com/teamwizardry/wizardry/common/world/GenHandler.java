package com.teamwizardry.wizardry.common.world;

import com.google.common.primitives.Ints;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.block.fluid.ModFluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

@Mod.EventBusSubscriber
public class GenHandler {

	private static void generateMana(World world, Random rand, int x, int z) {
		for (int i = 0; i < 1; i++) {
			WorldGenManaLake gen = new WorldGenManaLake(ModFluids.MANA.getActualBlock());
			int xRand = x * 16 + rand.nextInt(16);
			int zRand = z * 16 + rand.nextInt(16);
			int yRand = world.getChunk(x, z).getLowestHeight();
//			int yRand = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z)).getY();
			yRand = RandUtil.nextInt(yRand - 1, yRand);
			BlockPos position = new BlockPos(xRand, yRand, zRand);
			gen.generate(world, rand, position);
		}
	}

	@SubscribeEvent
	public static void gen(DecorateBiomeEvent.Pre event) {
		if (ConfigValues.manaPoolRarity > 0)
			if (ConfigValues.isDimBlacklist ^ Ints.contains(ConfigValues.manaPoolDimWhitelist, event.getWorld().provider.getDimension()))
				generateMana(event.getWorld(), event.getRand(), event.getChunkPos().x, event.getChunkPos().z);
	}
}
