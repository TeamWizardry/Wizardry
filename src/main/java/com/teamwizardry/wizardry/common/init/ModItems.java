package com.teamwizardry.wizardry.common.init;

import com.teamwizardry.librarianlib.foundation.registration.ItemSpec;
import com.teamwizardry.librarianlib.foundation.registration.RegistrationManager;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.item.ItemNacrePearl;
import com.teamwizardry.wizardry.common.item.ItemStaff;
import com.teamwizardry.wizardry.common.lib.LibItemNames;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Rarity;
import net.minecraftforge.fml.common.Mod;

public class ModItems {
	public static Item wisdomStick = new Item(defaultBuilder());
	public static Item staff;
	public static Item nacrePearl = new ItemNacrePearl(defaultBuilder());


	public static Item.Properties defaultBuilder() {
		return new Item.Properties().group(Wizardry.INSTANCE.getRegistrationManager().getDefaultItemGroup());
	}

	public static void initializeItems(RegistrationManager reggie) {
		reggie.add(new ItemSpec(LibItemNames.STAFF).maxStackSize(1).rarity(Rarity.UNCOMMON).item(itemSpec -> new ItemStaff(itemSpec.getItemProperties())));
	}

	public static void initializeItemGroup() {
		Wizardry.INSTANCE.getRegistrationManager().setDefaultItemGroupIcon(staff);
	}
}
