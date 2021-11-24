package com.teamwizardry.wizardry.common.init;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.block.BlockWisdomSapling;
import com.teamwizardry.wizardry.common.block.BlockWisdomSapling.WisdomSaplingGenerator;
import com.teamwizardry.wizardry.common.block.BlockWorktable;
import com.teamwizardry.wizardry.common.block.access.Invokers.DoorBlock;
import com.teamwizardry.wizardry.common.block.access.Invokers.StairsBlock;
import com.teamwizardry.wizardry.common.block.entity.craftingplate.BlockCraftingPlate;
import com.teamwizardry.wizardry.common.block.entity.craftingplate.BlockCraftingPlateEntity;
import com.teamwizardry.wizardry.common.block.entity.manabattery.BlockManaBattery;
import com.teamwizardry.wizardry.common.block.entity.manabattery.BlockManaBatteryEntity;
import com.teamwizardry.wizardry.common.block.fluid.mana.BlockMana;
import com.teamwizardry.wizardry.common.block.fluid.nacre.BlockNacre;
import com.teamwizardry.wizardry.mixins.BlocksMixin;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlocks {

    private static Settings wisdomWoodSettings = Settings.of(Material.WOOD, MapColor.BROWN).sounds(BlockSoundGroup.WOOD).strength(2);
    
    public static Block craftingPlate = new BlockCraftingPlate(Settings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).strength(2).solidBlock(BlocksMixin::never));
    public static BlockEntityType<BlockCraftingPlateEntity> craftingPlateEntity;
    public static Block magiciansWorktable = new BlockWorktable(Settings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).strength(2).solidBlock(BlocksMixin::never));
    public static Block manaBattery = new BlockManaBattery(Settings.of(Material.GLASS).sounds(BlockSoundGroup.AMETHYST_BLOCK).strength(3).solidBlock(BlocksMixin::never).luminance(state -> 15));
    public static BlockEntityType<BlockManaBatteryEntity> manaBatteryEntity;

    public static Block wisdomLog = new PillarBlock(wisdomWoodSettings);
    public static Block wisdomWood = new Block(wisdomWoodSettings);
    public static Block wisdomStrippedLog = new PillarBlock(wisdomWoodSettings);
    public static Block wisdomStrippedWood = new Block(wisdomWoodSettings);
    public static Block wisdomPlanks = new Block(wisdomWoodSettings);
    public static Block wisdomLeaves = new LeavesBlock(Settings.of(Material.LEAVES).strength(0.2F).ticksRandomly().sounds(BlockSoundGroup.GRASS).nonOpaque().allowsSpawning(BlocksMixin::canSpawnOnLeaves).suffocates(BlocksMixin::never).blockVision(BlocksMixin::never));
    public static Block wisdomDoor = new DoorBlock(wisdomWoodSettings.nonOpaque());
    public static Block wisdomSlab = new SlabBlock(wisdomWoodSettings);
    public static Block wisdomStairs = new StairsBlock(wisdomPlanks.getDefaultState(), wisdomWoodSettings);
    public static Block wisdomFence = new FenceBlock(wisdomWoodSettings);
    public static Block wisdomFenceGate = new FenceGateBlock(wisdomWoodSettings);
    public static Block wisdomSapling = new BlockWisdomSapling(new WisdomSaplingGenerator(), FabricBlockSettings.copyOf(Blocks.OAK_SAPLING));

    public static Block wisdomGildedPlanks;
    public static Block wisdomGildedSlab;
    public static Block wisdomGildedStairs;
    public static Block wisdomGildedFence;
    public static Block wisdomGildedFenceGate;

    public static Block nacreBlock;
    public static Block nacreSlab;
    public static Block nacreStairs;
    public static Block nacreFence;
    public static Block nacreFenceGate;

    public static Block nacreBrickBlock;
    public static Block nacreBrickSlab;
    public static Block nacreBrickStairs;
    public static Block nacreBrickFence;
    public static Block nacreBrickFenceGate;

    // Fluids
    public static FluidBlock liquidMana = new BlockMana(ModFluids.STILL_MANA, FabricBlockSettings.copy(Blocks.WATER));;
    public static FluidBlock liquidNacre = new BlockNacre(ModFluids.STILL_NACRE, FabricBlockSettings.copy(Blocks.WATER));;


    public static void init() {
        ///////////////////////////////
        // Basic Blocks
        ///////////////////////////////

        ////////////////
        // Wisdom Wood
        ////////////////

        // Base
        register(wisdomLog, "wisdom_log");
        register(wisdomWood, "wisdom_wood");
        register(wisdomStrippedLog, "stripped_wisdom_log");
        register(wisdomStrippedWood, "stripped_wisdom_wood");
        register(wisdomPlanks, "wisdom_planks");
        register(wisdomLeaves, "wisdom_leaves");
        register(wisdomDoor, "wisdom_door");
        register(wisdomSlab, "wisdom_slab");
        register(wisdomStairs, "wisdom_stairs");
        register(wisdomFence, "wisdom_fence");
        register(wisdomFenceGate, "wisdom_gate");
        register(wisdomSapling, "wisdom_sapling");

        // Gilded
//        BuildingBlockCollection wisdomGildedCollection = new BuildingBlockCollection("gilded_wisdom_wood_planks", "gilded_wisdom_wood");
//        wisdomGildedCollection.getBlockProperties()
//                .material(Material.WOOD)
//                .mapColor(MaterialColor.WOOD)
//                .sound(SoundType.WOOD)
//                .hardnessAndResistance(2f);

        // Non-Group variants
        

        ////////////////
        // Nacre
        ////////////////
//        nacreCollection.getBlockProperties()
//                .material(Material.ROCK)
//                .mapColor(MaterialColor.STONE)
//                .sound(SoundType.STONE)
//                .hardnessAndResistance(2f);
//
//        nacreBrickCollection.getBlockProperties()
//                .material(Material.ROCK)
//                .mapColor(MaterialColor.STONE)
//                .sound(SoundType.STONE)
//                .hardnessAndResistance(2f);

        ///////////////////////////////
        // Fluids
        ///////////////////////////////
        
        Registry.register(Registry.BLOCK, Wizardry.getId("mana"), liquidMana);
        Registry.register(Registry.BLOCK, Wizardry.getId("nacre"), liquidNacre);

        ///////////////////////////////
        // Complex Blocks
        ///////////////////////////////

        // Has a GUI and stuff, but no TE
        register(magiciansWorktable, "worktable");

        //////////////////
        // Block Entities
        //////////////////

        register(craftingPlate, "crafting_plate");
        register(manaBattery, "mana_battery");
    }

    public static void initBlockEntities() {
        craftingPlateEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                                                Wizardry.getId("crafting_plate"), 
                                                FabricBlockEntityTypeBuilder.create(BlockCraftingPlateEntity::new, craftingPlate)
                                                                            .build());
        manaBatteryEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                                              Wizardry.getId("mana_battery"),
                                              FabricBlockEntityTypeBuilder.create(BlockManaBatteryEntity::new, manaBattery)
                                                                          .build());
    }

//    public static void registerBlockColors(ColorHandlerEvent.Block event) {
//        event.getBlockColors().register((blockState, lightReader, pos, color) ->
//                        lightReader != null && pos != null
//                                ? BiomeColors.getFoliageColor(lightReader, pos)
//                                : FoliageColors.getDefault(),
//                wisdomLeaves.get());
//    }

//    @SubscribeEvent
//    public static void registerItemBlockColors(ColorHandlerEvent.Item event) {
//        event.getItemColors().register((stack, color) -> {
//                    BlockState blockstate = ((BlockItem) stack.getItem()).getBlock().getDefaultState();
//                    return event.getBlockColors().getColor(blockstate, null, null, color);
//                },
//                wisdomLeaves.get());
//    }
    
    private static void register(Block block, String path) {
        Identifier id = Wizardry.getId(path);
        Registry.register(Registry.BLOCK, id, block);
        Registry.register(Registry.ITEM, id, new BlockItem(block, new Item.Settings().group(ModItems.wizardry)));
    }
}
