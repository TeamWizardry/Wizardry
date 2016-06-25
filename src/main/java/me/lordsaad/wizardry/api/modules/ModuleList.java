package me.lordsaad.wizardry.api.modules;

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
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author murapix
 *         <p>
 *         Created on June 21, 2016
 */
public enum ModuleList
{
	INSTANCE;

	public Map<ResourceLocation, IModuleConstructor> modules;
	public SortedMap<ItemStack, ResourceLocation> moduleItems;

	private void register(String name, IModuleConstructor constructor)
	{
		modules.put(new ResourceLocation(Wizardry.MODID, name), constructor);
	}

	private void item(String name, Block block)
	{
		item(name, block, 1);
	}

	private void item(String name, Block block, int amount)
	{
		item(name, new ItemStack(block, amount));
	}

	private void item(String name, Item item)
	{
		item(name, item, 1);
	}

	private void item(String name, Item item, int amount)
	{
		item(name, new ItemStack(item, amount));
	}
	
	private void item(String name, ItemStack stack) {
		moduleItems.put(stack, new ResourceLocation(Wizardry.MODID, name));
	}

	public Module createModule(ItemStack stack) {
		for (ItemStack test : moduleItems.keySet()) {
			if(match(test, stack))
				return modules.get(moduleItems.get(test)).construct();
		}
		return null;
	}
	
	public boolean match(ItemStack a, ItemStack b) {
		if(a == b)
			return true;
		
		if(a == null || b == null)
			return false;
		
		if(a.getItem() != b.getItem())
			return false;
		
		if(a.getItemDamage() == OreDictionary.WILDCARD_VALUE)
			return true;
		
		if(b.getItemDamage() == OreDictionary.WILDCARD_VALUE)
			return true;

		return a.getItemDamage() == b.getItemDamage();

	}

	public void init()
	{
		modules = new HashMap<>();
		moduleItems = new TreeMap<>((a, b) -> -Integer.compare(a == null ? 0 : a.stackSize, b == null ? 0 : b.stackSize));

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
		register("modifierSilent", ModuleSilent::new);
		register("modifierDuration", ModuleDuration::new);
		register("modifierManaCost", ModuleManaCost::new);
		register("modifierBurnOut", ModuleBurnOut::new);
		register("modifierArea", ModuleArea::new);
		register("modifierPierce", ModulePierce::new);
		register("modifierBeamModifier", ModuleBeamModifier::new);
		register("modifierRangedDamage", ModuleRangedDamage::new);
		register("modifierPunch", ModulePunch::new);
		register("modifierSticky", ModuleSticky::new);
		register("modifierScatter", ModuleScatter::new);
		register("modifierProjectileCount", ModuleProjectileCount::new);
		register("modifierMeleeDamage", ModuleMeleeDamage::new);
		register("modifierCritChance", ModuleCritChance::new);
		register("modifierMagicDamage", ModuleMagicDamage::new);
		register("modifierEnchantment", ModuleEnchantment::new);

		item("modifierSilent", new ItemStack(Blocks.WOOL, 16, OreDictionary.WILDCARD_VALUE));
		item("modifierDuration", Blocks.SAND);
		item("modifierManaCost", new ItemStack(Items.DYE, 64, 4));
		item("modifierBurnOut", Items.SUGAR, 64);
		item("modifierArea", Items.DRAGON_BREATH);
		item("modifierPierce", new ItemStack(Blocks.GLASS, 16, OreDictionary.WILDCARD_VALUE));
		item("modifierBeamModifier", Items.PRISMARINE_CRYSTALS);
		item("modifierRangedDamage", Items.ARROW, 16);
		item("modifierPunch", Items.SNOWBALL, 100);
		item("modifierSticky", Items.SLIME_BALL, 16);
		item("modifierScatter", Blocks.GRAVEL);
		item("modifierProjectileCount", Items.QUARTZ);
		item("modifierMeleeDamage", Items.DIAMOND, 3);
		item("modifierCritChance", Items.RABBIT_FOOT);
		item("modifierMagicDamage", Items.GOLD_INGOT);
		item("modifierEnchantment", Items.ENCHANTED_BOOK);
		
		// Shape Modules
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

	@FunctionalInterface
	public interface IModuleConstructor
	{
		Module construct();
	}
}