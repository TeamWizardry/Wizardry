package com.teamwizardry.wizardry.common.init;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.lib.LibItemNames;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = Wizardry.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemInit {

	public static final Item wisdomStick = new Item(defaultBuilder());


	public static Item.Properties defaultBuilder() {
		return new Item.Properties().group(ModItemGroup.INSTANCE);
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event){
		IForgeRegistry<Item> r = event.getRegistry();

		r.register(wisdomStick.setRegistryName(Wizardry.MODID, LibItemNames.WISDOM_STICK));

	}
}
