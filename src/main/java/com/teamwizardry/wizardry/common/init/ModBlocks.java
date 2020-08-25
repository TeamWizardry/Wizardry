package com.teamwizardry.wizardry.common.init;

import com.teamwizardry.librarianlib.foundation.registration.BlockSpec;
import com.teamwizardry.librarianlib.foundation.registration.LazyBlock;
import com.teamwizardry.librarianlib.foundation.registration.RegistrationManager;
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
public class ModBlocks {
	public static final LazyBlock craftingPlate = new LazyBlock();

	public static void registerBlocks(RegistrationManager reggie) {
		craftingPlate.from(reggie.add(new BlockSpec(LibBlockNames.CRAFTING_PLATE).block(blockSpec -> new BlockCraftingPlate(blockSpec.getBlockProperties()))));
	}

	@SubscribeEvent
	public static void registerTile(RegistryEvent.Register<TileEntityType<?>> event) {
		IForgeRegistry<TileEntityType<?>> r = event.getRegistry();

		TileEntityType<?> type = TileEntityType.Builder.create(TileCraftingPlate::new, craftingPlate.get()).build(null);

		r.register(type.setRegistryName(Wizardry.MODID, LibBlockNames.CRAFTING_PLATE));
	}
}
