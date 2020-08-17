package com.teamwizardry.wizardry.common.init;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.block.BlockCraftingPlate;
import com.teamwizardry.wizardry.common.lib.LibBlockNames;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = Wizardry.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockInit {

	public static final Block CRAFTING_PLATE = new BlockCraftingPlate(Block.Properties.create(Material.WOOD));

	@SubscribeEvent
	public static void registerBlock(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();

		r.register(CRAFTING_PLATE.setRegistryName(Wizardry.MODID, LibBlockNames.CRAFTING_PLATE));
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		r.register(new BlockItem(CRAFTING_PLATE, new Item.Properties().group(ModItemGroup.INSTANCE)).setRegistryName(CRAFTING_PLATE.getRegistryName()));

	}

	@SubscribeEvent
	public static void registerTile(RegistryEvent.Register<TileEntityType<?>> event) {
		IForgeRegistry<TileEntityType<?>> r = event.getRegistry();

		TileEntityType<?> type = TileEntityType.Builder.create(TileCraftingPlate::new, CRAFTING_PLATE).build(null);

		r.register(type.setRegistryName(Wizardry.MODID, LibBlockNames.CRAFTING_PLATE));
	}
}
