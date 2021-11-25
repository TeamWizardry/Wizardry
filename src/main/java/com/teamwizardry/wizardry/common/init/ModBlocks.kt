package com.teamwizardry.wizardry.common.init

import com.teamwizardry.wizardry.Wizardry
import com.teamwizardry.wizardry.common.block.BlockWisdomSapling
import com.teamwizardry.wizardry.common.block.BlockWisdomSapling.WisdomSaplingGenerator
import com.teamwizardry.wizardry.common.block.BlockWorktable
import com.teamwizardry.wizardry.common.block.access.Invokers
import com.teamwizardry.wizardry.common.block.entity.craftingplate.BlockCraftingPlate
import com.teamwizardry.wizardry.common.block.entity.craftingplate.BlockCraftingPlateEntity
import com.teamwizardry.wizardry.common.block.entity.manabattery.BlockManaBattery
import com.teamwizardry.wizardry.common.block.entity.manabattery.BlockManaBatteryEntity
import com.teamwizardry.wizardry.common.block.fluid.mana.BlockMana
import com.teamwizardry.wizardry.common.block.fluid.nacre.BlockNacre
import com.teamwizardry.wizardry.mixins.BlocksMixin
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.color.world.BiomeColors
import net.minecraft.client.color.world.FoliageColors
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object ModBlocks {
    private val wisdomWoodSettings = AbstractBlock.Settings.of(Material.WOOD, MapColor.BROWN).sounds(BlockSoundGroup.WOOD).strength(2f)
    private val gildedWoodSettings = AbstractBlock.Settings.of(Material.WOOD, MapColor.BROWN).sounds(BlockSoundGroup.WOOD).strength(2f)
    private val nacreSettings = AbstractBlock.Settings.of(Material.STONE, MapColor.IRON_GRAY).sounds(BlockSoundGroup.STONE).strength(2f)
    private val nacreBrickSettings = AbstractBlock.Settings.of(Material.STONE, MapColor.IRON_GRAY).sounds(BlockSoundGroup.STONE).strength(2f)

    var craftingPlate: Block = BlockCraftingPlate(AbstractBlock.Settings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).strength(2f).solidBlock(BlocksMixin::never))
    var craftingPlateEntity: BlockEntityType<BlockCraftingPlateEntity>? = null
    var magiciansWorktable: Block = BlockWorktable(AbstractBlock.Settings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).strength(2f).solidBlock(BlocksMixin::never))
    var manaBattery: Block = BlockManaBattery(AbstractBlock.Settings.of(Material.GLASS).sounds(BlockSoundGroup.AMETHYST_BLOCK).strength(3f).solidBlock(BlocksMixin::never).luminance { state: BlockState? -> 15 })
    var manaBatteryEntity: BlockEntityType<BlockManaBatteryEntity>? = null

    var wisdomLeaves: Block = LeavesBlock(AbstractBlock.Settings.of(Material.LEAVES).strength(0.2f).ticksRandomly().sounds(BlockSoundGroup.GRASS).nonOpaque().allowsSpawning(BlocksMixin::canSpawnOnLeaves).suffocates(BlocksMixin::never).blockVision(BlocksMixin::never))
    var wisdomLog: Block = PillarBlock(wisdomWoodSettings)
    var wisdomWood = Block(wisdomWoodSettings)
    var wisdomStrippedLog: Block = PillarBlock(wisdomWoodSettings)
    var wisdomStrippedWood = Block(wisdomWoodSettings)
    var wisdomPlanks = Block(wisdomWoodSettings)
    var wisdomDoor: Block = Invokers.DoorBlock(wisdomWoodSettings.nonOpaque())
    var wisdomSlab: Block = SlabBlock(wisdomWoodSettings)
    var wisdomStairs: Block = Invokers.StairsBlock(wisdomPlanks.defaultState, wisdomWoodSettings)
    var wisdomFence: Block = FenceBlock(wisdomWoodSettings)
    var wisdomFenceGate: Block = FenceGateBlock(wisdomWoodSettings)

    var wisdomSapling: Block = BlockWisdomSapling(WisdomSaplingGenerator(), FabricBlockSettings.copyOf(Blocks.OAK_SAPLING))

    var gildedPlanks: Block = Block(gildedWoodSettings)
    var gildedDoor: Block = Invokers.DoorBlock(gildedWoodSettings)
    var gildedSlab: Block = SlabBlock(gildedWoodSettings)
    var gildedStairs: Block = Invokers.StairsBlock(gildedPlanks.defaultState, gildedWoodSettings)
    var gildedFence: Block = FenceBlock(gildedWoodSettings)
    var gildedFenceGate: Block = FenceGateBlock(gildedWoodSettings)

    var nacreBlock: Block = Block(nacreSettings)
    var nacreSlab: Block = SlabBlock(nacreSettings)
    var nacreStairs: Block = Invokers.StairsBlock(nacreBlock.defaultState, nacreSettings)
    var nacreWall: Block = WallBlock(nacreSettings)

    var nacreBrickBlock: Block = Block(nacreSettings)
    var nacreBrickSlab: Block = SlabBlock(nacreSettings)
    var nacreBrickStairs: Block = Invokers.StairsBlock(nacreBlock.defaultState, nacreSettings)
    var nacreBrickWall: Block = WallBlock(nacreSettings)

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

        ColorProviderRegistry.BLOCK.register({_, world, pos, _ -> if (world != null && pos != null) BiomeColors.getFoliageColor(world, pos) else FoliageColors.getDefaultColor() }, wisdomLeaves)
        ColorProviderRegistry.ITEM.register({_, _ -> FoliageColors.getDefaultColor() }, wisdomLeaves)

        // Gilded
        register(gildedPlanks, "gilded_wisdom_planks")
        register(gildedDoor, "gilded_wisdom_door")
        register(gildedSlab, "gilded_wisdom_slab")
        register(gildedStairs, "gilded_wisdom_stairs")
        register(gildedFence, "gilded_wisdom_fence")
        register(gildedFenceGate, "gilded_wisdom_gate")

        ////////////////
        // Nacre
        ////////////////

        register(nacreBlock, "nacre_block")
        register(nacreSlab, "nacre_slab")
        register(nacreStairs, "nacre_stairs")
        register(nacreWall, "narce_wall")

        register(nacreBrickBlock, "nacre_brick_block")
        register(nacreBrickSlab, "nacre_brick_slab")
        register(nacreBrickStairs, "nacre_brick_stairs")
        register(nacreBrickWall, "nacre_brick_wall")

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
            FabricBlockEntityTypeBuilder.create(FabricBlockEntityTypeBuilder.Factory(::BlockCraftingPlateEntity), craftingPlate).build()
        )
        manaBatteryEntity = Registry.register<BlockEntityType<*>, BlockEntityType<BlockManaBatteryEntity>>(
            Registry.BLOCK_ENTITY_TYPE,
            Wizardry.getId("mana_battery"),
            FabricBlockEntityTypeBuilder.create(FabricBlockEntityTypeBuilder.Factory(::BlockManaBatteryEntity), manaBattery).build()
        )
    }

    private fun register(block: Block, path: String) {
        val id: Identifier = Wizardry.getId(path)
        Registry.register(Registry.BLOCK, id, block)
        Registry.register(Registry.ITEM, id, BlockItem(block, Item.Settings().group(ModItems.wizardry)))
    }
}