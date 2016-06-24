package me.lordsaad.wizardry.api.modules;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import me.lordsaad.wizardry.Wizardry;
import me.lordsaad.wizardry.spells.modules.booleans.ModuleAnd;
import me.lordsaad.wizardry.spells.modules.booleans.ModuleNand;
import me.lordsaad.wizardry.spells.modules.booleans.ModuleNor;
import me.lordsaad.wizardry.spells.modules.booleans.ModuleOr;
import me.lordsaad.wizardry.spells.modules.effects.ModuleBlink;
import me.lordsaad.wizardry.spells.modules.effects.ModuleExplosion;
import me.lordsaad.wizardry.spells.modules.effects.ModuleFallProtection;
import me.lordsaad.wizardry.spells.modules.effects.ModuleFlame;
import me.lordsaad.wizardry.spells.modules.effects.ModuleLava;
import me.lordsaad.wizardry.spells.modules.effects.ModuleLight;
import me.lordsaad.wizardry.spells.modules.effects.ModulePotion;
import me.lordsaad.wizardry.spells.modules.effects.ModuleSaturation;
import me.lordsaad.wizardry.spells.modules.effects.ModuleWater;
import me.lordsaad.wizardry.spells.modules.events.ModuleBlinkEvent;
import me.lordsaad.wizardry.spells.modules.events.ModuleFallEvent;
import me.lordsaad.wizardry.spells.modules.events.ModuleMeleeEvent;
import me.lordsaad.wizardry.spells.modules.events.ModuleOnFireEvent;
import me.lordsaad.wizardry.spells.modules.events.ModulePotionEvent;
import me.lordsaad.wizardry.spells.modules.events.ModuleRangedEvent;
import me.lordsaad.wizardry.spells.modules.events.ModuleSuffocationEvent;
import me.lordsaad.wizardry.spells.modules.events.ModuleUnderwaterEvent;
import me.lordsaad.wizardry.spells.modules.modifiers.ModuleArea;
import me.lordsaad.wizardry.spells.modules.modifiers.ModuleBeamModifier;
import me.lordsaad.wizardry.spells.modules.modifiers.ModuleBurnOut;
import me.lordsaad.wizardry.spells.modules.modifiers.ModuleCritChance;
import me.lordsaad.wizardry.spells.modules.modifiers.ModuleDuration;
import me.lordsaad.wizardry.spells.modules.modifiers.ModuleEnchantment;
import me.lordsaad.wizardry.spells.modules.modifiers.ModuleMagicDamage;
import me.lordsaad.wizardry.spells.modules.modifiers.ModuleManaCost;
import me.lordsaad.wizardry.spells.modules.modifiers.ModuleMeleeDamage;
import me.lordsaad.wizardry.spells.modules.modifiers.ModuleProjectileCount;
import me.lordsaad.wizardry.spells.modules.modifiers.ModulePunch;
import me.lordsaad.wizardry.spells.modules.modifiers.ModuleRangedDamage;
import me.lordsaad.wizardry.spells.modules.modifiers.ModuleScatter;
import me.lordsaad.wizardry.spells.modules.modifiers.ModuleSilent;
import me.lordsaad.wizardry.spells.modules.modifiers.ModuleSticky;
import me.lordsaad.wizardry.spells.modules.shapes.ModuleBeam;
import me.lordsaad.wizardry.spells.modules.shapes.ModuleCone;
import me.lordsaad.wizardry.spells.modules.shapes.ModuleMelee;
import me.lordsaad.wizardry.spells.modules.shapes.ModuleProjectile;
import me.lordsaad.wizardry.spells.modules.shapes.ModuleSelf;
import me.lordsaad.wizardry.spells.modules.shapes.ModuleZone;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import com.google.common.collect.HashBiMap;

/**
 * @author murapix
 *         <p>
 *         Created on June 21, 2016
 */
public enum ModuleList
{
	INSTANCE;
	
	public Map<ResourceLocation, IModuleConstructor> modules;
	public HashBiMap<Predicate<ItemStack>, ResourceLocation> moduleItems;

	@FunctionalInterface
	public static interface IModuleConstructor {
		public Module construct();
	}

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
			if(pred.test(stack))
				return modules.get( moduleItems.get(pred) ).construct();
		}
		return null;
	}
	
	public void init()
	{
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
		register("silent", ModuleSilent::new);
		register("duration", ModuleDuration::new);
		register("manaCost", ModuleManaCost::new);
		register("burnout", ModuleBurnOut::new);
		register("area", ModuleArea::new);
		register("pierce", ModuleArea::new);
		register("modifierBeam", ModuleBeamModifier::new);
		register("rangedDamage", ModuleRangedDamage::new);
		register("punch", ModulePunch::new);
		register("sticky", ModuleSticky::new);
		register("scatter", ModuleScatter::new);
		register("projectileCount", ModuleProjectileCount::new);
		register("meleeDamage", ModuleMeleeDamage::new);
		register("critChance", ModuleCritChance::new);
		register("magicDamage", ModuleMagicDamage::new);
		register("enchantment", ModuleEnchantment::new);
		
		item("silent", new ItemStack(Blocks.WOOL, 16, OreDictionary.WILDCARD_VALUE));
		item("duration", Blocks.SAND);
		item("manaCost", new ItemStack(Items.DYE, 64, 4));
		item("burnout", new ItemStack(Items.SUGAR, 64));
		item("area", Items.DRAGON_BREATH);
		item("pierce", new ItemStack(Blocks.GLASS, 16, OreDictionary.WILDCARD_VALUE));
		item("modifierBeam", Items.PRISMARINE_CRYSTALS);
		item("rangedDamage", new ItemStack(Items.ARROW, 16));
		item("punch", new ItemStack(Items.SNOWBALL, 100));
		item("sticky", new ItemStack(Items.SLIME_BALL, 16));
		item("scatter", Blocks.GRAVEL);
		item("projectileCount", new ItemStack(Items.QUARTZ, 16));
		item("meleeDamage", new ItemStack(Items.DIAMOND, 3));
		item("critChance", Items.RABBIT_FOOT);
		item("magicDamage", Items.GOLD_INGOT);
		item("enchantment", Items.ENCHANTED_BOOK);

		// Shapes
		register("shapeBeam", ModuleBeam::new);
		register("shapeProjectile", ModuleProjectile::new);
		register("shapeMelee", ModuleMelee::new);
		register("shapeSelf", ModuleSelf::new);
		register("shapeZone", ModuleZone::new);
		register("shapeCone", ModuleCone::new);
		
		item("shapeBeam", Items.PRISMARINE_SHARD);
		item("shapeProjectile", Items.BOW);
		item("shapeMelee", Items.DIAMOND_SWORD);
		item("shapeSelf", Items.GOLDEN_APPLE);
		item("shapeZone", Blocks.GLASS_PANE);
		item("shapeCone", Items.GUNPOWDER);
	}
}