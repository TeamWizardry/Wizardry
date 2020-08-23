package com.teamwizardry.wizardry.common.init;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.item.ItemNacrePearl;
import com.teamwizardry.wizardry.common.item.ItemStaff;
import com.teamwizardry.wizardry.common.lib.LibItemNames;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = Wizardry.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemInit {

	public static final Item wisdomStick = new Item(defaultBuilder());
	public static final Item staff = new ItemStaff(defaultBuilder());
	public static final Item nacrePearl = new ItemNacrePearl(defaultBuilder());


	public static Item.Properties defaultBuilder() {
		return new Item.Properties().group(Wizardry.INSTANCE.getRegistrationManager().getDefaultItemGroup());
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event){
		initializeItemGroup();

		IForgeRegistry<Item> r = event.getRegistry();

		r.register(wisdomStick.setRegistryName(Wizardry.MODID, LibItemNames.WISDOM_STICK));
		r.register(staff.setRegistryName(Wizardry.MODID, LibItemNames.STAFF));
		r.register(nacrePearl.setRegistryName(Wizardry.MODID, LibItemNames.NACRE_PEARL));
	}

	private static void initializeItemGroup() {
		ItemGroup group = Wizardry.INSTANCE.getRegistrationManager().getDefaultItemGroup();

		Wizardry.INSTANCE.getRegistrationManager().setDefaultItemGroupIcon(staff);
	}
}
