package com.teamwizardry.wizardry.api.module;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.common.spell.module.booleans.ModuleAnd;
import com.teamwizardry.wizardry.common.spell.module.booleans.ModuleNand;
import com.teamwizardry.wizardry.common.spell.module.booleans.ModuleNor;
import com.teamwizardry.wizardry.common.spell.module.booleans.ModuleOr;
import com.teamwizardry.wizardry.common.spell.module.effects.ModuleBlink;
import com.teamwizardry.wizardry.common.spell.module.effects.ModuleExplosion;
import com.teamwizardry.wizardry.common.spell.module.effects.ModuleFallProtection;
import com.teamwizardry.wizardry.common.spell.module.effects.ModuleFlame;
import com.teamwizardry.wizardry.common.spell.module.effects.ModuleLava;
import com.teamwizardry.wizardry.common.spell.module.effects.ModuleLight;
import com.teamwizardry.wizardry.common.spell.module.effects.ModulePotion;
import com.teamwizardry.wizardry.common.spell.module.effects.ModuleSaturation;
import com.teamwizardry.wizardry.common.spell.module.effects.ModuleWater;
import com.teamwizardry.wizardry.common.spell.module.events.ModuleBlinkEvent;
import com.teamwizardry.wizardry.common.spell.module.events.ModuleFallEvent;
import com.teamwizardry.wizardry.common.spell.module.events.ModuleMeleeEvent;
import com.teamwizardry.wizardry.common.spell.module.events.ModuleOnFireEvent;
import com.teamwizardry.wizardry.common.spell.module.events.ModulePotionEvent;
import com.teamwizardry.wizardry.common.spell.module.events.ModuleRangedEvent;
import com.teamwizardry.wizardry.common.spell.module.events.ModuleSuffocationEvent;
import com.teamwizardry.wizardry.common.spell.module.events.ModuleUnderwaterEvent;
import com.teamwizardry.wizardry.common.spell.module.modifiers.ModuleArea;
import com.teamwizardry.wizardry.common.spell.module.modifiers.ModuleBeamModifier;
import com.teamwizardry.wizardry.common.spell.module.modifiers.ModuleBurnOut;
import com.teamwizardry.wizardry.common.spell.module.modifiers.ModuleCritChance;
import com.teamwizardry.wizardry.common.spell.module.modifiers.ModuleDuration;
import com.teamwizardry.wizardry.common.spell.module.modifiers.ModuleEnchantment;
import com.teamwizardry.wizardry.common.spell.module.modifiers.ModuleMagicDamage;
import com.teamwizardry.wizardry.common.spell.module.modifiers.ModuleManaCost;
import com.teamwizardry.wizardry.common.spell.module.modifiers.ModuleMeleeDamage;
import com.teamwizardry.wizardry.common.spell.module.modifiers.ModulePierce;
import com.teamwizardry.wizardry.common.spell.module.modifiers.ModuleProjectileCount;
import com.teamwizardry.wizardry.common.spell.module.modifiers.ModulePunch;
import com.teamwizardry.wizardry.common.spell.module.modifiers.ModuleRangedDamage;
import com.teamwizardry.wizardry.common.spell.module.modifiers.ModuleScatter;
import com.teamwizardry.wizardry.common.spell.module.modifiers.ModuleSilent;
import com.teamwizardry.wizardry.common.spell.module.modifiers.ModuleSticky;
import com.teamwizardry.wizardry.common.spell.module.shapes.ModuleBeam;
import com.teamwizardry.wizardry.common.spell.module.shapes.ModuleCone;
import com.teamwizardry.wizardry.common.spell.module.shapes.ModuleMelee;
import com.teamwizardry.wizardry.common.spell.module.shapes.ModuleProjectile;
import com.teamwizardry.wizardry.common.spell.module.shapes.ModuleSelf;
import com.teamwizardry.wizardry.common.spell.module.shapes.ModuleZone;
import com.teamwizardry.wizardry.common.spell.parsing.StackWrapper;

/**
 * @author murapix
 *         <p>
 *         Created on June 21, 2016
 */
public enum ModuleList
{
	INSTANCE;

	public SortedMap<StackWrapper, String> booleanItems;
	public SortedMap<StackWrapper, String> effectItems;
	public SortedMap<StackWrapper, String> eventItems;
	public SortedMap<StackWrapper, String> modifierItems;
	public SortedMap<StackWrapper, String> shapeItems;
	public Map<String, IModuleConstructor> modules;

	ModuleList()
	{
		modules = new HashMap<>();
		booleanItems = new TreeMap<>();
		effectItems = new TreeMap<>();
		eventItems = new TreeMap<>();
		modifierItems = new TreeMap<>();
		shapeItems = new TreeMap<>();

		// Booleans
		register("boolAND", ModuleAnd::new);
		register("boolOR", ModuleOr::new);
		register("boolNAND", ModuleNand::new);
		register("boolNOR", ModuleNor::new);

		item("boolAND", ModuleType.BOOLEAN, Items.STRING);
		item("boolOR", ModuleType.BOOLEAN, Items.WHEAT_SEEDS);
		item("boolNAND", ModuleType.BOOLEAN, Blocks.REDSTONE_TORCH);
		item("boolNOR", ModuleType.BOOLEAN, Blocks.TORCH);

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

		item("blink", ModuleType.EFFECT, Items.CHORUS_FRUIT_POPPED);
		item("explosion", ModuleType.EFFECT, Blocks.TNT);
		item("fallProtection", ModuleType.EFFECT, Blocks.HAY_BLOCK, 32);
		item("flame", ModuleType.EFFECT, Items.BLAZE_POWDER);
		item("lava", ModuleType.EFFECT, Items.LAVA_BUCKET);
		item("light", ModuleType.EFFECT, Blocks.GLOWSTONE);
		item("potion", ModuleType.EFFECT, new ItemStack(Items.POTIONITEM, 6, OreDictionary.WILDCARD_VALUE));
		item("saturation", ModuleType.EFFECT, Items.CAKE);
		item("water", ModuleType.EFFECT, Items.WATER_BUCKET);

		// Events
		register("eventMelee", ModuleMeleeEvent::new);
		register("eventRanged", ModuleRangedEvent::new);
		register("eventUnderwater", ModuleUnderwaterEvent::new);
		register("eventSuffocation", ModuleSuffocationEvent::new);
		register("eventFall", ModuleFallEvent::new);
		register("eventOnFire", ModuleOnFireEvent::new);
		register("eventBlink", ModuleBlinkEvent::new);
		register("eventPotion", ModulePotionEvent::new);

		item("eventMelee", ModuleType.EVENT, Items.IRON_SWORD);
		item("evenRanged", ModuleType.EVENT, Items.BOW);
		item("eventUnderwater", ModuleType.EVENT, Items.FISH);
		item("eventSuffocation", ModuleType.EVENT, new ItemStack(Items.FISH, 1, 3));
		item("eventFall", ModuleType.EVENT, Items.FEATHER);
		item("eventOnFire", ModuleType.EVENT, Items.FLINT);
		item("eventBlink", ModuleType.EVENT, Items.ENDER_PEARL);
		item("eventPotion", ModuleType.EVENT, Items.GLASS_BOTTLE);

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

		item("modifierSilent", ModuleType.MODIFIER, new ItemStack(Blocks.WOOL, 16, OreDictionary.WILDCARD_VALUE));
		item("modifierDuration", ModuleType.MODIFIER, Blocks.SAND);
		item("modifierManaCost", ModuleType.MODIFIER, new ItemStack(Items.DYE, 64, 4));
		item("modifierBurnOut", ModuleType.MODIFIER, Items.SUGAR, 64);
		item("modifierArea", ModuleType.MODIFIER, Items.DRAGON_BREATH);
		item("modifierPierce", ModuleType.MODIFIER, new ItemStack(Blocks.GLASS, 16, OreDictionary.WILDCARD_VALUE));
		item("modifierBeamModifier", ModuleType.MODIFIER, Items.PRISMARINE_CRYSTALS);
		item("modifierRangedDamage", ModuleType.MODIFIER, Items.ARROW, 16);
		item("modifierPunch", ModuleType.MODIFIER, Items.SNOWBALL, 100);
		item("modifierSticky", ModuleType.MODIFIER, Items.SLIME_BALL, 16);
		item("modifierScatter", ModuleType.MODIFIER, Blocks.GRAVEL);
		item("modifierProjectileCount", ModuleType.MODIFIER, Items.QUARTZ);
		item("modifierMeleeDamage", ModuleType.MODIFIER, Items.DIAMOND, 3);
		item("modifierCritChance", ModuleType.MODIFIER, Items.RABBIT_FOOT);
		item("modifierMagicDamage", ModuleType.MODIFIER, Items.GOLD_INGOT);
		item("modifierEnchantment", ModuleType.MODIFIER, Items.ENCHANTED_BOOK);

		// Shape Modules
		register("shapeBeam", ModuleBeam::new);
		register("shapeProjectile", ModuleProjectile::new);
		register("shapeMelee", ModuleMelee::new);
		register("shapeSelf", ModuleSelf::new);
		register("shapeZone", ModuleZone::new);
		register("shapeCone", ModuleCone::new);

		item("shapeBeam", ModuleType.SHAPE, Items.PRISMARINE_SHARD);
		item("shapeProjectile", ModuleType.SHAPE, Items.BOW);
		item("shapeMelee", ModuleType.SHAPE, Items.DIAMOND_SWORD);
		item("shapeSelf", ModuleType.SHAPE, Items.GOLDEN_APPLE);
		item("shapeZone", ModuleType.SHAPE, Blocks.GLASS_PANE);
		item("shapeCone", ModuleType.SHAPE, Items.GUNPOWDER);
	}

	private void register(String name, IModuleConstructor constructor)
	{
		modules.put(name, constructor);
	}

	private void item(String name, ModuleType type, Block block)
	{
		item(name, type, block, 1);
	}

	private void item(String name, ModuleType type, Block block, int amount)
	{
		item(name, type, new ItemStack(block, amount));
	}

	private void item(String name, ModuleType type, Item item)
	{
		item(name, type, item, 1);
	}

	private void item(String name, ModuleType type, Item item, int amount)
	{
		item(name, type, new ItemStack(item, amount));
	}

	private void item(String name, ModuleType type, ItemStack stack)
	{
		switch (type)
		{
			case BOOLEAN:
				booleanItems.put(new StackWrapper(stack), name);
				break;
			case EFFECT:
				effectItems.put(new StackWrapper(stack), name);
				break;
			case EVENT:
				eventItems.put(new StackWrapper(stack), name);
				break;
			case MODIFIER:
				modifierItems.put(new StackWrapper(stack), name);
				break;
			case SHAPE:
				shapeItems.put(new StackWrapper(stack), name);
				break;
		}
	}

	public Module createModule(ItemStack stack, ModuleType type)
	{
		SortedMap<StackWrapper, String> map;
		switch (type)
		{
			case BOOLEAN:
				map = booleanItems;
				break;
			case EFFECT:
				map = effectItems;
				break;
			case EVENT:
				map = eventItems;
				break;
			case MODIFIER:
				map = modifierItems;
				break;
			case SHAPE:
				map = shapeItems;
				break;
			default: return null;
		}
		for (StackWrapper test : map.keySet())
		{
			if (test.equals(stack))
				return modules.get(map.get(test)).construct();
		}
		return null;
	}

	@FunctionalInterface
	public interface IModuleConstructor
	{
		Module construct();
	}
}