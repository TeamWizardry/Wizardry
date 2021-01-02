package com.teamwizardry.wizardry.common.init;

import com.teamwizardry.librarianlib.foundation.block.BaseLogBlock;
import com.teamwizardry.librarianlib.foundation.registration.*;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.block.BlockCraftingPlate;
import com.teamwizardry.wizardry.common.block.BlockManaBattery;
import com.teamwizardry.wizardry.common.block.BlockWisdomSapling;
import com.teamwizardry.wizardry.common.block.BlockWorktable;
import com.teamwizardry.wizardry.common.block.fluid.mana.BlockMana;
import com.teamwizardry.wizardry.common.lib.LibBlockNames;
import com.teamwizardry.wizardry.common.lib.LibTileEntityType;
import com.teamwizardry.wizardry.common.structure.WisdomTree;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;

import com.teamwizardry.wizardry.common.tile.TileManaBattery;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.FoliageColors;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

@Mod.EventBusSubscriber(modid = Wizardry.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBlocks {
    public static final LazyBlock craftingPlate = new LazyBlock();
    public static final LazyBlock magiciansWorktable = new LazyBlock();
    public static final LazyBlock manaBattery = new LazyBlock();

    public static final LazyBlock wisdomLog = new LazyBlock();
    public static final LazyBlock wisdomPlanks = new LazyBlock();
    public static final LazyBlock wisdomLeaves = new LazyBlock();
    public static final LazyBlock wisdomDoor = new LazyBlock();
    public static final LazyBlock wisdomSlab = new LazyBlock();
    public static final LazyBlock wisdomStairs = new LazyBlock();
    public static final LazyBlock wisdomFence = new LazyBlock();
    public static final LazyBlock wisdomFenceGate = new LazyBlock();
    public static final LazyBlock wisdomSapling = new LazyBlock();

    public static final LazyBlock wisdomGildedPlanks = new LazyBlock();
    public static final LazyBlock wisdomGildedSlab = new LazyBlock();
    public static final LazyBlock wisdomGildedStairs = new LazyBlock();
    public static final LazyBlock wisdomGildedFence = new LazyBlock();
    public static final LazyBlock wisdomGildedFenceGate = new LazyBlock();

    public static final LazyBlock nacreBlock = new LazyBlock();
    public static final LazyBlock nacreSlab = new LazyBlock();
    public static final LazyBlock nacreStairs = new LazyBlock();
    public static final LazyBlock nacreFence = new LazyBlock();
    public static final LazyBlock nacreFenceGate = new LazyBlock();

    public static final LazyBlock nacreBrickBlock = new LazyBlock();
    public static final LazyBlock nacreBrickSlab = new LazyBlock();
    public static final LazyBlock nacreBrickStairs = new LazyBlock();
    public static final LazyBlock nacreBrickFence = new LazyBlock();
    public static final LazyBlock nacreBrickFenceGate = new LazyBlock();

    // Fluids
    public static final LazyBlock liquidMana = new LazyBlock();


    public static void registerBlocks(RegistrationManager reggie) {
        ///////////////////////////////
        // Basic Blocks
        ///////////////////////////////

        ////////////////
        // Wisdom Wood
        ////////////////

        // Planks
        BuildingBlockCollection wisdomPlanksCollection = new BuildingBlockCollection("wisdom_wood_planks", "wisdom_wood");
        wisdomPlanksCollection.getBlockProperties()
                .material(Material.WOOD)
                .mapColor(MaterialColor.WOOD)
                .sound(SoundType.WOOD)
                .hardnessAndResistance(2f);

        wisdomPlanks.from(reggie.add(wisdomPlanksCollection.getFull()));
        wisdomSlab.from(reggie.add(wisdomPlanksCollection.getSlab()));
        wisdomStairs.from(reggie.add(wisdomPlanksCollection.getStairs()));
        wisdomFence.from(reggie.add(wisdomPlanksCollection.getFence()));
        wisdomFenceGate.from(reggie.add(wisdomPlanksCollection.getFenceGate()));

        // Gilded
        BuildingBlockCollection wisdomGildedCollection = new BuildingBlockCollection("gilded_wisdom_wood_planks", "gilded_wisdom_wood");
        wisdomGildedCollection.getBlockProperties()
                .material(Material.WOOD)
                .mapColor(MaterialColor.WOOD)
                .sound(SoundType.WOOD)
                .hardnessAndResistance(2f);

        wisdomGildedPlanks.from(reggie.add(wisdomGildedCollection.getFull()));
        wisdomGildedSlab.from(reggie.add(wisdomGildedCollection.getSlab()));
        wisdomGildedStairs.from(reggie.add(wisdomGildedCollection.getStairs()));
        wisdomGildedFence.from(reggie.add(wisdomGildedCollection.getFence()));
        wisdomGildedFenceGate.from(reggie.add(wisdomGildedCollection.getFenceGate()));

        // Non-Group variants
        wisdomLog.from(reggie.add(new BlockSpec(LibBlockNames.WISDOM_LOG)
                .block(blockSpec -> new BaseLogBlock(MaterialColor.BROWN, blockSpec.getBlockProperties()))
                .withProperties(BaseLogBlock.DEFAULT_PROPERTIES)));

        wisdomLeaves.from(reggie.add(new BlockSpec(LibBlockNames.WISDOM_LEAVES)
                .material(Material.LEAVES)
                .hardnessAndResistance(0.2F).tickRandomly().sound(SoundType.PLANT).notSolid()
                .block(blockSpec -> new LeavesBlock(blockSpec.getBlockProperties().getVanillaProperties()))));

        wisdomSapling.from(reggie.add(new BlockSpec(LibBlockNames.WISDOM_SAPLING)
                .material(Material.PLANTS)
                .doesNotBlockMovement()
                .tickRandomly()
                .hardnessAndResistance(0.0f)
                .sound(SoundType.PLANT)
                .renderLayer(RenderLayerSpec.CUTOUT_MIPPED)
                .block(blockSpec -> new BlockWisdomSapling(new WisdomTree(), blockSpec.getBlockProperties().getVanillaProperties()))));

        ////////////////
        // Nacre
        ////////////////
        BuildingBlockCollection nacreCollection = new BuildingBlockCollection("nacre_block", "nacre_block");
        nacreCollection.getBlockProperties()
                .material(Material.ROCK)
                .mapColor(MaterialColor.STONE)
                .sound(SoundType.STONE)
                .hardnessAndResistance(2f);

        nacreBlock.from(reggie.add(nacreCollection.getFull()));
        nacreSlab.from(reggie.add(nacreCollection.getSlab()));
        nacreStairs.from(reggie.add(nacreCollection.getStairs()));
        nacreFence.from(reggie.add(nacreCollection.getFence()));
        nacreFenceGate.from(reggie.add(nacreCollection.getFenceGate()));

        BuildingBlockCollection nacreBrickCollection = new BuildingBlockCollection("nacre_block_bricks", "nacre_block_brick");
        nacreBrickCollection.getBlockProperties()
                .material(Material.ROCK)
                .mapColor(MaterialColor.STONE)
                .sound(SoundType.STONE)
                .hardnessAndResistance(2f);

        nacreBrickBlock.from(reggie.add(nacreBrickCollection.getFull()));
        nacreBrickSlab.from(reggie.add(nacreBrickCollection.getSlab()));
        nacreBrickStairs.from(reggie.add(nacreBrickCollection.getStairs()));
        nacreBrickFence.from(reggie.add(nacreBrickCollection.getFence()));
        nacreBrickFenceGate.from(reggie.add(nacreBrickCollection.getFenceGate()));


        ///////////////////////////////
        // Fluids
        ///////////////////////////////
        liquidMana.from(reggie.add(new BlockSpec(LibBlockNames.MANA_FLUID)
                .material(Material.WATER)
                .doesNotBlockMovement()
                .lightValue(12)
                .hardnessAndResistance(100.0F)
                .noDrops()
                .block(blockSpec -> new BlockMana(() -> ModFluids.MANA_FLUID, blockSpec.getBlockProperties().getVanillaProperties()))));


        ///////////////////////////////
        // Complex Blocks
        ///////////////////////////////

        // Has a GUI and stuff, but no TE
        magiciansWorktable.from(reggie.add(new BlockSpec(LibBlockNames.WORKTABLE)
                .material(Material.WOOD).hardnessAndResistance(2f).sound(SoundType.WOOD).notSolid()
                .block(blockSpec -> new BlockWorktable(blockSpec.getBlockProperties().getVanillaProperties()))));

        //////////////////
        // Tile Entities
        //////////////////
        craftingPlate.from(reggie.add(new BlockSpec(LibBlockNames.CRAFTING_PLATE)
                .material(Material.WOOD).hardnessAndResistance(2f).sound(SoundType.WOOD).notSolid()
                .tileEntity(LibTileEntityType.CRAFTING_PLATE)
                .block(blockSpec -> new BlockCraftingPlate(blockSpec.getBlockProperties().getVanillaProperties()))));

        manaBattery.from(reggie.add(new BlockSpec(LibBlockNames.MANA_BATTERY)
                .material(Material.GLASS).hardnessAndResistance(3f).sound(SoundType.GLASS).notSolid()
                .tileEntity(LibTileEntityType.MANA_BATTERY)
                .block(blockSpec -> new BlockManaBattery(blockSpec.getBlockProperties().getVanillaProperties()))));
    }

    public static void registerTile(RegistrationManager registrationManager) {
        LibTileEntityType.MANA_BATTERY.from(registrationManager.add(new TileEntitySpec<>("mana_battery", TileManaBattery::new)));
        LibTileEntityType.CRAFTING_PLATE.from(registrationManager.add(new TileEntitySpec<>("crafting_plate", TileCraftingPlate::new)));
    }

    @SubscribeEvent
    public static void registerBlockColors(ColorHandlerEvent.Block event) {
        event.getBlockColors().register((blockState, lightReader, pos, color) ->
                        lightReader != null && pos != null
                                ? BiomeColors.getFoliageColor(lightReader, pos)
                                : FoliageColors.getDefault(),
                wisdomLeaves.get());
    }

    @SubscribeEvent
    public static void registerItemBlockColors(ColorHandlerEvent.Item event) {
        event.getItemColors().register((stack, color) -> {
                    BlockState blockstate = ((BlockItem) stack.getItem()).getBlock().getDefaultState();
                    return event.getBlockColors().getColor(blockstate, null, null, color);
                },
                wisdomLeaves.get());
    }
}
