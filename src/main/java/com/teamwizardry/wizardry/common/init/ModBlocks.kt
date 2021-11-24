package com.teamwizardry.wizardry.common.init

import com.teamwizardry.wizardry.Wizardry
import com.teamwizardry.wizardry.common.block.BlockWisdomSapling
import com.teamwizardry.wizardry.common.block.BlockWisdomSapling.WisdomSaplingGenerator
import com.teamwizardry.wizardry.common.block.BlockWorktable
import com.teamwizardry.wizardry.common.block.entity.craftingplate.BlockCraftingPlate
import com.teamwizardry.wizardry.common.block.entity.craftingplate.BlockCraftingPlateEntity
import com.teamwizardry.wizardry.common.block.entity.manabattery.BlockManaBattery
import com.teamwizardry.wizardry.common.block.entity.manabattery.BlockManaBatteryEntity
import com.teamwizardry.wizardry.common.block.fluid.mana.BlockMana
import com.teamwizardry.wizardry.common.block.fluid.nacre.BlockNacre
import com.teamwizardry.wizardry.mixins.BlocksMixin
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.EntityType
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.world.BlockView

object ModBlocks {
    private val wisdomWoodSettings =
        AbstractBlock.Settings.of(Material.WOOD, MapColor.BROWN).sounds(BlockSoundGroup.WOOD).strength(2f)
    private var craftingPlate: Block = BlockCraftingPlate(
        AbstractBlock.Settings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).strength(2f)
            .solidBlock { state: BlockState?, world: BlockView?, pos: BlockPos? ->
                BlocksMixin.never(
                    state,
                    world,
                    pos
                )
            })
    var craftingPlateEntity: BlockEntityType<BlockCraftingPlateEntity>? = null
    private var magiciansWorktable: Block = BlockWorktable(
        AbstractBlock.Settings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).strength(2f)
            .solidBlock { state: BlockState?, world: BlockView?, pos: BlockPos? ->
                BlocksMixin.never(
                    state,
                    world,
                    pos
                )
            })
    private var manaBattery: Block = BlockManaBattery(
        AbstractBlock.Settings.of(Material.GLASS).sounds(BlockSoundGroup.AMETHYST_BLOCK).strength(3f)
            .solidBlock { state: BlockState?, world: BlockView?, pos: BlockPos? ->
                BlocksMixin.never(
                    state,
                    world,
                    pos
                )
            }
            .luminance { state: BlockState? -> 15 })
    var manaBatteryEntity: BlockEntityType<BlockManaBatteryEntity>? = null
    var wisdomLog: Block = PillarBlock(wisdomWoodSettings)
    private var wisdomWood = Block(wisdomWoodSettings)
    private var wisdomStrippedLog: Block = PillarBlock(wisdomWoodSettings)
    private var wisdomStrippedWood = Block(wisdomWoodSettings)
    private var wisdomPlanks = Block(wisdomWoodSettings)
    var wisdomLeaves: Block = LeavesBlock(
        AbstractBlock.Settings.of(Material.LEAVES).strength(0.2f).ticksRandomly().sounds(BlockSoundGroup.GRASS)
            .nonOpaque().allowsSpawning { state: BlockState?, world: BlockView?, pos: BlockPos?, type: EntityType<*>? ->
                BlocksMixin.canSpawnOnLeaves(
                    state,
                    world,
                    pos,
                    type
                )!!
            }
            .suffocates { state: BlockState?, world: BlockView?, pos: BlockPos? ->
                BlocksMixin.never(
                    state,
                    world,
                    pos
                )
            }
            .blockVision { state: BlockState?, world: BlockView?, pos: BlockPos? ->
                BlocksMixin.never(
                    state,
                    world,
                    pos
                )
            })
    private var wisdomDoor: Block = DoorBlock(wisdomWoodSettings.nonOpaque())
    private var wisdomSlab: Block = SlabBlock(wisdomWoodSettings)
    private var wisdomStairs: Block = StairsBlock(wisdomPlanks.defaultState, wisdomWoodSettings)
    private var wisdomFence: Block = FenceBlock(wisdomWoodSettings)
    private var wisdomFenceGate: Block = FenceGateBlock(wisdomWoodSettings)
    var wisdomSapling: Block =
        BlockWisdomSapling(WisdomSaplingGenerator(), FabricBlockSettings.copyOf(Blocks.OAK_SAPLING))
    var wisdomGildedPlanks: Block? = null
    var wisdomGildedSlab: Block? = null
    var wisdomGildedStairs: Block? = null
    var wisdomGildedFence: Block? = null
    var wisdomGildedFenceGate: Block? = null
    var nacreBlock: Block? = null
    var nacreSlab: Block? = null
    var nacreStairs: Block? = null
    var nacreFence: Block? = null
    var nacreFenceGate: Block? = null
    var nacreBrickBlock: Block? = null
    var nacreBrickSlab: Block? = null
    var nacreBrickStairs: Block? = null
    var nacreBrickFence: Block? = null
    var nacreBrickFenceGate: Block? = null

    // Fluids
    var liquidMana: FluidBlock = BlockMana(ModFluids.STILL_MANA, FabricBlockSettings.copy(Blocks.WATER))
    var liquidNacre: FluidBlock = BlockNacre(ModFluids.STILL_NACRE, FabricBlockSettings.copy(Blocks.WATER))
    fun init() {
        ///////////////////////////////
        // Basic Blocks
        ///////////////////////////////

        ////////////////
        // Wisdom Wood
        ////////////////

        // Base
        register(wisdomLog, "wisdom_log")
        register(wisdomWood, "wisdom_wood")
        register(wisdomStrippedLog, "stripped_wisdom_log")
        register(wisdomStrippedWood, "stripped_wisdom_wood")
        register(wisdomPlanks, "wisdom_planks")
        register(wisdomLeaves, "wisdom_leaves")
        register(wisdomDoor, "wisdom_door")
        register(wisdomSlab, "wisdom_slab")
        register(wisdomStairs, "wisdom_stairs")
        register(wisdomFence, "wisdom_fence")
        register(wisdomFenceGate, "wisdom_gate")
        register(wisdomSapling, "wisdom_sapling")

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
        Registry.register(Registry.BLOCK, Wizardry.getId("mana"), liquidMana)
        Registry.register(Registry.BLOCK, Wizardry.getId("nacre"), liquidNacre)

        ///////////////////////////////
        // Complex Blocks
        ///////////////////////////////

        // Has a GUI and stuff, but no TE
        register(magiciansWorktable, "worktable")

        //////////////////
        // Block Entities
        //////////////////
        register(craftingPlate, "crafting_plate")
        register(manaBattery, "mana_battery")
    }

    fun initBlockEntities() {
        craftingPlateEntity = Registry.register<BlockEntityType<*>, BlockEntityType<BlockCraftingPlateEntity>>(
            Registry.BLOCK_ENTITY_TYPE,
            Wizardry.getId("crafting_plate"),
            FabricBlockEntityTypeBuilder.create(FabricBlockEntityTypeBuilder.Factory { pos: BlockPos?, state: BlockState? ->
                BlockCraftingPlateEntity(
                    pos,
                    state
                )
            }, craftingPlate)
                .build()
        )
        manaBatteryEntity = Registry.register<BlockEntityType<*>, BlockEntityType<BlockManaBatteryEntity>>(
            Registry.BLOCK_ENTITY_TYPE,
            Wizardry.getId("mana_battery"),
            FabricBlockEntityTypeBuilder.create(FabricBlockEntityTypeBuilder.Factory { pos: BlockPos?, state: BlockState? ->
                BlockManaBatteryEntity(
                    pos,
                    state
                )
            }, manaBattery)
                .build()
        )
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
    private fun register(block: Block, path: String) {
        val id: Identifier = Wizardry.getId(path)
        Registry.register(Registry.BLOCK, id, block)
        Registry.register(
            Registry.ITEM,
            id,
            BlockItem(block, Item.Settings().group(ModItems.wizardry))
        )
    }
}