package com.teamwizardry.wizardry.common.init;

import com.teamwizardry.librarianlib.foundation.block.BaseLogBlock;
import com.teamwizardry.librarianlib.foundation.registration.BlockSpec;
import com.teamwizardry.librarianlib.foundation.registration.LazyBlock;
import com.teamwizardry.librarianlib.foundation.registration.RegistrationManager;
import com.teamwizardry.librarianlib.foundation.registration.RenderLayerSpec;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.block.BlockCraftingPlate;
import com.teamwizardry.wizardry.common.block.BlockWisdomSapling;
import com.teamwizardry.wizardry.common.block.BlockWorktable;
import com.teamwizardry.wizardry.common.block.fluid.mana.BlockMana;
import com.teamwizardry.wizardry.common.lib.LibBlockNames;
import com.teamwizardry.wizardry.common.structure.WisdomTree;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.FoliageColors;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = Wizardry.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBlocks {
    public static final LazyBlock craftingPlate = new LazyBlock();
    public static final LazyBlock magiciansWorktable = new LazyBlock();

    public static final LazyBlock wisdomLog = new LazyBlock();
    public static final LazyBlock wisdomPlanks = new LazyBlock();
    public static final LazyBlock wisdomGildedPlanks = new LazyBlock();
    public static final LazyBlock wisdomLeaves = new LazyBlock();
    public static final LazyBlock wisdomDoor = new LazyBlock();
    public static final LazyBlock wisdomFence = new LazyBlock();
    public static final LazyBlock wisdomSapling = new LazyBlock();

    // Fluids
    public static final LazyBlock liquidMana = new LazyBlock();


    public static void registerBlocks(RegistrationManager reggie) {
        // Basic Blocks

        // Wisdom Wood
        wisdomLog.from(reggie.add(new BlockSpec(LibBlockNames.WISDOM_LOG)
                .block(blockSpec -> new BaseLogBlock(MaterialColor.BROWN, blockSpec.getBlockProperties()))
                .withProperties(BaseLogBlock.DEFAULT_PROPERTIES)));
        wisdomPlanks.from(reggie.add(new BlockSpec(LibBlockNames.WISDOM_PLANKS)
                .block(blockSpec -> new Block(blockSpec.getBlockProperties()))
                .withProperties(BaseLogBlock.DEFAULT_PROPERTIES)));
        wisdomGildedPlanks.from(reggie.add(new BlockSpec(LibBlockNames.WISDOM_GILDED_PLANKS)
                .block(blockSpec -> new Block(blockSpec.getBlockProperties()))
                .withProperties(BaseLogBlock.DEFAULT_PROPERTIES)));
        wisdomLeaves.from(reggie.add(new BlockSpec(LibBlockNames.WISDOM_LEAVES)
                .material(Material.LEAVES)
                .hardnessAndResistance(0.2F).tickRandomly().sound(SoundType.PLANT).notSolid()
                .block(blockSpec -> new LeavesBlock(blockSpec.getBlockProperties()))));
        wisdomSapling.from(reggie.add(new BlockSpec(LibBlockNames.WISDOM_SAPLING)
                .material(Material.PLANTS)
                .doesNotBlockMovement()
                .tickRandomly()
                .hardnessAndResistance(0.0f)
                .sound(SoundType.PLANT)
                .renderLayer(RenderLayerSpec.CUTOUT_MIPPED)
                .block(blockSpec -> new BlockWisdomSapling(new WisdomTree(), blockSpec.getBlockProperties()))));

        // Fluids
        liquidMana.from(reggie.add(new BlockSpec(LibBlockNames.MANA_FLUID)
                .material(Material.WATER)
                .doesNotBlockMovement()
                .lightValue(12)
                .hardnessAndResistance(100.0F)
                .noDrops()
                .block(blockSpec -> new BlockMana(() -> ModFluids.MANA_FLUID, blockSpec.getBlockProperties()))));

        // Tile Entities
        craftingPlate.from(reggie.add(new BlockSpec(LibBlockNames.CRAFTING_PLATE)
                .material(Material.WOOD).hardnessAndResistance(2f).sound(SoundType.WOOD).notSolid()
                .block(blockSpec -> new BlockCraftingPlate(blockSpec.getBlockProperties()))));
        magiciansWorktable.from(reggie.add(new BlockSpec(LibBlockNames.WORKTABLE)
                .material(Material.WOOD).hardnessAndResistance(2f).sound(SoundType.WOOD).notSolid()
                .block(blockSpec -> new BlockWorktable(blockSpec.getBlockProperties()))));
    }

    @SubscribeEvent
    public static void registerTile(RegistryEvent.Register<TileEntityType<?>> event) {
        IForgeRegistry<TileEntityType<?>> r = event.getRegistry();

        TileEntityType<?> type = TileEntityType.Builder.create(TileCraftingPlate::new, craftingPlate.get()).build(null);

        r.register(type.setRegistryName(Wizardry.MODID, LibBlockNames.CRAFTING_PLATE));
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
