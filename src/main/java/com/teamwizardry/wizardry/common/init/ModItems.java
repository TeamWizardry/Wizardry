package com.teamwizardry.wizardry.common.init;

import com.teamwizardry.librarianlib.foundation.registration.ItemSpec;
import com.teamwizardry.librarianlib.foundation.registration.LazyItem;
import com.teamwizardry.librarianlib.foundation.registration.RegistrationManager;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.item.ItemNacrePearl;
import com.teamwizardry.wizardry.common.item.ItemStaff;
import com.teamwizardry.wizardry.common.lib.LibItemNames;
import net.minecraft.item.EnchantedGoldenAppleItem;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.potion.*;
import org.lwjgl.system.CallbackI;
import org.spongepowered.api.effect.potion.PotionEffect;

import java.util.function.Supplier;

public class ModItems {
	public static final LazyItem wisdomStick = new LazyItem();
	public static final LazyItem staff = new LazyItem();
	public static final LazyItem nacrePearl = new LazyItem();
	public static final LazyItem devilDust = new LazyItem();
	public static final LazyItem skyDust = new LazyItem();
	public static final LazyItem fairyDust = new LazyItem();
	public static final LazyItem fairyWings = new LazyItem();
	public static final LazyItem fairyApple = new LazyItem();

	public static void initializeItems(RegistrationManager reggie) {
		wisdomStick.from(reggie.add(new ItemSpec(LibItemNames.WISDOM_STICK)));
		devilDust.from(reggie.add(new ItemSpec(LibItemNames.DEVIL_DUST).rarity(Rarity.UNCOMMON)));
		skyDust.from(reggie.add(new ItemSpec(LibItemNames.SKY_DUST).rarity(Rarity.UNCOMMON)));
		fairyDust.from(reggie.add(new ItemSpec(LibItemNames.FAIRY_DUST).rarity(Rarity.UNCOMMON)));
		fairyWings.from(reggie.add(new ItemSpec(LibItemNames.FAIRY_WINGS).rarity(Rarity.UNCOMMON)));

		fairyApple.from(reggie.add(new ItemSpec(LibItemNames.FAIRY_APPLE).rarity(Rarity.RARE).food(
				new Food.Builder()
						.setAlwaysEdible()
						.hunger(5)
						.saturation(10)
						.effect(() -> new EffectInstance(Effects.REGENERATION, 20 * 15), 1f)
						.effect(() -> new EffectInstance(Effects.JUMP_BOOST, 20 * 60 * 2, 1), 1f)
						.effect(() -> new EffectInstance(Effects.GLOWING, 20*30), 1f)
						.effect(() -> new EffectInstance(Effects.SPEED, 20 * 60 * 2, 1), 1f)
						.effect(() -> new EffectInstance(Effects.NIGHT_VISION, 20 * 60 * 2), 1f)
						.build())));

		staff.from(reggie.add(new ItemSpec(LibItemNames.STAFF).maxStackSize(1).rarity(Rarity.UNCOMMON).item(itemSpec -> new ItemStaff(itemSpec.getItemProperties()))));
		nacrePearl.from(reggie.add(new ItemSpec(LibItemNames.NACRE_PEARL).maxStackSize(1).rarity(Rarity.UNCOMMON).item(itemSpec -> new ItemNacrePearl(itemSpec.getItemProperties()))));
	}

	public static void initializeItemGroup() {
		Wizardry.INSTANCE.getRegistrationManager().setItemGroupIcon(staff);
	}
}
