package com.teamwizardry.wizardry.common.init;

import com.teamwizardry.librarianlib.foundation.block.BaseLogBlock;
import com.teamwizardry.librarianlib.foundation.registration.BlockSpec;
import com.teamwizardry.librarianlib.foundation.registration.DefaultProperties;
import com.teamwizardry.librarianlib.foundation.registration.LazyBlock;
import com.teamwizardry.librarianlib.foundation.registration.RegistrationManager;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.block.BlockCraftingPlate;
import com.teamwizardry.wizardry.common.block.BlockWisdomLeaves;
import com.teamwizardry.wizardry.common.lib.LibBlockNames;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = Wizardry.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBlocks {
	public static final LazyBlock craftingPlate = new LazyBlock();
	public static final LazyBlock wisdomLog = new LazyBlock();
	public static final LazyBlock wisdomPlanks = new LazyBlock();
	public static final LazyBlock wisdomGildedPlanks = new LazyBlock();
	public static final LazyBlock wisdomLeaves = new LazyBlock();

	public static void registerBlocks(RegistrationManager reggie) {
		// Basic Blocks

		// Wisdom Wood
		wisdomLog.from(reggie.add(new BlockSpec(LibBlockNames.WISDOM_LOG).withProperties(BaseLogBlock.DEFAULT_PROPERTIES)));
		wisdomPlanks.from(reggie.add(new BlockSpec(LibBlockNames.WISDOM_PLANKS).withProperties(BaseLogBlock.DEFAULT_PROPERTIES)));
		wisdomPlanks.from(reggie.add(new BlockSpec(LibBlockNames.WISDOM_GILDED_PLANKS).withProperties(BaseLogBlock.DEFAULT_PROPERTIES)));
		wisdomLeaves.from(reggie.add(new BlockSpec(LibBlockNames.WISDOM_LEAVES).block(blockSpec -> new BlockWisdomLeaves(Block.Properties.create(Material.LEAVES)))));


		// Tile Entities
		craftingPlate.from(reggie.add(new BlockSpec(LibBlockNames.CRAFTING_PLATE).block(blockSpec -> new BlockCraftingPlate(blockSpec.getBlockProperties()))));
	}

	@SubscribeEvent
	public static void registerTile(RegistryEvent.Register<TileEntityType<?>> event) {
		IForgeRegistry<TileEntityType<?>> r = event.getRegistry();

		TileEntityType<?> type = TileEntityType.Builder.create(TileCraftingPlate::new, craftingPlate.get()).build(null);

		r.register(type.setRegistryName(Wizardry.MODID, LibBlockNames.CRAFTING_PLATE));
	}
}
