package me.lordsaad.wizardry.api.modules;

import com.google.common.collect.HashBiMap;
import me.lordsaad.wizardry.Wizardry;
import me.lordsaad.wizardry.spells.modules.booleans.ModuleAnd;
import me.lordsaad.wizardry.spells.modules.booleans.ModuleNand;
import me.lordsaad.wizardry.spells.modules.booleans.ModuleNor;
import me.lordsaad.wizardry.spells.modules.booleans.ModuleOr;
import me.lordsaad.wizardry.spells.modules.effects.*;
import me.lordsaad.wizardry.spells.modules.events.*;
import me.lordsaad.wizardry.spells.modules.modifiers.*;
import me.lordsaad.wizardry.spells.modules.shapes.*;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author murapix
 *         <p>
 *         Created on June 21, 2016
 */
public enum ModuleList {
	INSTANCE;

	public Map<ResourceLocation, IModuleConstructor> modules;
	public HashBiMap<Predicate<ItemStack>, ResourceLocation> moduleItems;

	private void register(String name, IModuleConstructor constructor) {
		modules.put(new ResourceLocation(Wizardry.MODID, name), constructor);
	}

	private void item(String name, Block block) {
		item(name, block, 1);
	}

	private void item(String name, Block block, int amount) {
		item(name, new ItemStack(block, amount));
	}

	private void item(String name, Item item) {
		item(name, item, 1);
	}

	private void item(String name, Item item, int amount) {
		item(name, new ItemStack(item, amount));
	}

	private void item(String name, ItemStack stack) {
		moduleItems.put(
				(itemstack) -> {
					return ItemStack.areItemsEqual(itemstack, stack) &&
							itemstack.stackSize == stack.stackSize &&
							itemstack.getItemDamage() == stack.getItemDamage();
				},
				new ResourceLocation(Wizardry.MODID, name)
		);
	}

	public Module createModule(ItemStack stack) {
		for (Predicate<ItemStack> pred : moduleItems.keySet()) {
			if (pred.test(stack))
				return modules.get(moduleItems.get(pred)).construct();
		}
		return null;
	}

	public void init() {
		modules = new HashMap<>();
		moduleItems = HashBiMap.create();

		// Booleans
		register("boolAND", ModuleAnd::new);
		register("boolOR", ModuleOr::new);
		register("boolNAND", ModuleNand::new);
		register("boolNOR", ModuleNor::new);

		item("boolAND", Items.STRING);
		item("boolOR", Items.WHEAT_SEEDS);
		item("boolNAND", Blocks.REDSTONE_TORCH);
		item("boolNOR", Blocks.TORCH);

		// Effects
		register("blink", ModuleBlink::new);
		register("explosion", ModuleExplosion::new);
		register("fallProtection", ModuleFallProtection::new);
		register("flame", ModuleFlame::new);
		register("lava", ModuleLava::new);
		register("light", ModuleLight::new);
		register("potion", ModulePotion::new);
		register("saturation", ModuleSaturation::new);
		register("water", ModuleWater::new);

		item("blink", Items.CHORUS_FRUIT_POPPED);
		item("explosion", Blocks.TNT);
		item("fallProtection", Blocks.HAY_BLOCK, 32);
		item("flame", Items.BLAZE_POWDER);
		item("lava", Items.LAVA_BUCKET);
		item("light", Blocks.GLOWSTONE);
		item("potion", new ItemStack(Items.POTIONITEM, 6, OreDictionary.WILDCARD_VALUE));
		item("saturation", Blocks.CAKE);
		item("water", Items.WATER_BUCKET);

		// Events
		register("eventMelee", ModuleMeleeEvent::new);
		register("eventRanged", ModuleRangedEvent::new);
		register("eventUnderwater", ModuleUnderwaterEvent::new);
		register("eventSuffocation", ModuleSuffocationEvent::new);
		register("eventFall", ModuleFallEvent::new);
		register("eventOnFire", ModuleOnFireEvent::new);
		register("eventBlink", ModuleBlinkEvent::new);
		register("eventPotion", ModulePotionEvent::new);


		item("eventMelee", Items.IRON_SWORD);
		item("evenRanged", Items.BOW);
		item("eventUnderwater", Items.FISH);
		item("eventSuffocation", new ItemStack(Items.FISH, 1, 3));
		item("eventFall", Items.FEATHER);
		item("eventOnFire", Items.FLINT);
		item("eventBlink", Items.ENDER_PEARL);
		item("eventPotion", Items.GLASS_BOTTLE);

		// Modifiers


		// Modifier Modules
		moduleItems.put(new ModuleSilent(), new ItemStack(Blocks.WOOL, 16, OreDictionary.WILDCARD_VALUE));
		moduleItems.put(new ModuleDuration(), new ItemStack(Blocks.SAND));
		moduleItems.put(new ModuleManaCost(), new ItemStack(Items.DYE, 64, 4));
		moduleItems.put(new ModuleBurnOut(), new ItemStack(Items.SUGAR, 64));
		moduleItems.put(new ModuleArea(), new ItemStack(Items.DRAGON_BREATH));
		moduleItems.put(new ModulePierce(), new ItemStack(Blocks.GLASS, 16, OreDictionary.WILDCARD_VALUE));
		moduleItems.put(new ModuleBeamModifier(), new ItemStack(Items.PRISMARINE_CRYSTALS));
		moduleItems.put(new ModuleRangedDamage(), new ItemStack(Items.ARROW, 16));
		moduleItems.put(new ModulePunch(), new ItemStack(Items.SNOWBALL, 100));
		moduleItems.put(new ModuleSticky(), new ItemStack(Items.SLIME_BALL, 16));
		moduleItems.put(new ModuleScatter(), new ItemStack(Blocks.GRAVEL));
		moduleItems.put(new ModuleProjectileCount(), new ItemStack(Items.QUARTZ));
		moduleItems.put(new ModuleMeleeDamage(), new ItemStack(Items.DIAMOND, 3));
		moduleItems.put(new ModuleCritChance(), new ItemStack(Items.RABBIT_FOOT));
		moduleItems.put(new ModuleMagicDamage(), new ItemStack(Items.GOLD_INGOT));
		moduleItems.put(new ModuleEnchantment(), new ItemStack(Items.ENCHANTED_BOOK));

		// Shape Modules
		moduleItems.put(new ModuleBeam(), new ItemStack(Items.PRISMARINE_SHARD));
		moduleItems.put(new ModuleProjectile(), new ItemStack(Items.BOW));
		moduleItems.put(new ModuleMelee(), new ItemStack(Items.DIAMOND_SWORD));
		moduleItems.put(new ModuleSelf(), new ItemStack(Items.GOLDEN_APPLE));
		moduleItems.put(new ModuleZone(), new ItemStack(Blocks.GLASS_PANE));
		moduleItems.put(new ModuleCone(), new ItemStack(Items.GUNPOWDER));
	}

	@FunctionalInterface
	public interface IModuleConstructor {
		Module construct();
	}
}