package com.teamwizardry.wizardry.init;


import com.teamwizardry.wizardry.common.block.*;
import com.teamwizardry.wizardry.common.block.fluid.ModFluids;
import com.teamwizardry.wizardry.common.block.wisdomwood.*;
import net.minecraft.block.Block;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Demoniaque on 3/24/2016.
 */
public class ModBlocks {

	public static BlockCraftingPlate CRAFTING_PLATE;
	public static BlockMagiciansWorktable MAGICIANS_WORKTABLE;
	public static BlockManaBattery MANA_BATTERY;
	public static BlockCreativeManaBattery CREATIVE_MANA_BATTERY;
	public static BlockPearlHolder PEARL_HOLDER;
	public static BlockHaloInfuser HALO_INFUSER;
	public static BlockCloud CLOUD;
	public static BlockManaMagnet MANA_MAGNET;
	public static BlockLight LIGHT;
	public static BlockJar JAR;

	public static BlockNacre NACRE;
	public static BlockNacreBrick NACRE_BRICK;

	public static BlockWisdomLeaves WISDOM_LEAVES;
	public static BlockWisdomWoodLog WISDOM_WOOD_LOG;
	public static BlockWisdomWoodPlanks WISDOM_WOOD_PLANKS;
	public static BlockWisdomWoodSlab WISDOM_WOOD_SLAB;
	public static BlockWisdomWoodStairs WISDOM_WOOD_STAIRS;
	public static BlockWisdomWoodTrapdoor WISDOM_WOOD_TRAPDOOR;
	public static BlockWisdomWoodDoor WISDOM_WOOD_DOOR;
	public static BlockWisdomWoodFence WISDOM_WOOD_FENCE;
	public static BlockWisdomWoodFenceGate WISDOM_WOOD_FENCE_GATE;
	public static BlockWisdomWoodPigmentedPlanks WISDOM_WOOD_PIGMENTED_PLANKS;

	public static BlockTorikkiGrass TORIKKI_GRASS;

	public static BlockUnicornTrail UNICORN_TRAIL;

	public static void init() {

		MinecraftForge.EVENT_BUS.register(ModBlocks.class);

		ModFluids.init();

		CRAFTING_PLATE = new BlockCraftingPlate();
		MAGICIANS_WORKTABLE = new BlockMagiciansWorktable();
		MANA_BATTERY = new BlockManaBattery();
		CREATIVE_MANA_BATTERY = new BlockCreativeManaBattery();
		PEARL_HOLDER = new BlockPearlHolder();
		HALO_INFUSER = new BlockHaloInfuser();
		CLOUD = new BlockCloud();
		NACRE = new BlockNacre();
		NACRE_BRICK = new BlockNacreBrick();
		MANA_MAGNET = new BlockManaMagnet();
		LIGHT = new BlockLight();
		JAR = new BlockJar();

		WISDOM_LEAVES = new BlockWisdomLeaves();
		WISDOM_WOOD_LOG = new BlockWisdomWoodLog();
		WISDOM_WOOD_PLANKS = new BlockWisdomWoodPlanks();
		WISDOM_WOOD_SLAB = new BlockWisdomWoodSlab();
		WISDOM_WOOD_STAIRS = new BlockWisdomWoodStairs();
		WISDOM_WOOD_TRAPDOOR = new BlockWisdomWoodTrapdoor();
		WISDOM_WOOD_DOOR = new BlockWisdomWoodDoor();
		WISDOM_WOOD_FENCE = new BlockWisdomWoodFence();
		WISDOM_WOOD_FENCE_GATE = new BlockWisdomWoodFenceGate();
		WISDOM_WOOD_PIGMENTED_PLANKS = new BlockWisdomWoodPigmentedPlanks();

		TORIKKI_GRASS = new BlockTorikkiGrass();

		UNICORN_TRAIL = new BlockUnicornTrail();
	}

	@SubscribeEvent
	public static void remapFluids(RegistryEvent.MissingMappings<Block> event) {
		event.getMappings().stream()
				.filter(mapping -> mapping.key.getResourcePath().equals("mana"))
				.forEach(mapping -> mapping.remap(ModFluids.MANA.getActualBlock()));
		event.getMappings().stream()
				.filter(mapping -> mapping.key.getResourcePath().equals("nacre"))
				.forEach(mapping -> mapping.remap(ModFluids.NACRE.getActualBlock()));
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void registerModels(ModelRegistryEvent event) {
		PEARL_HOLDER.initModel();
		JAR.initModel();
		MANA_MAGNET.initModel();
	}
}
