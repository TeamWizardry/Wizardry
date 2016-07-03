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
 *         <p>
 *         Use Wizardry.moduleList to access the static copy
 */
public class ModuleList
{
	public SortedMap<StackWrapper, String> booleanItems;
	public SortedMap<StackWrapper, String> effectItems;
	public SortedMap<StackWrapper, String> eventItems;
	public SortedMap<StackWrapper, String> modifierItems;
	public SortedMap<StackWrapper, String> shapeItems;
	public Map<String, IModuleConstructor> modules;

	public void init()
	{
		modules = new HashMap<>();
		booleanItems = new TreeMap<>();
		effectItems = new TreeMap<>();
		eventItems = new TreeMap<>();
		modifierItems = new TreeMap<>();
		shapeItems = new TreeMap<>();

		// Booleans
		register(Items.STRING, ModuleAnd::new);
		register(Items.WHEAT_SEEDS, ModuleOr::new);
		register(Blocks.REDSTONE_TORCH, ModuleNand::new);
		register(Blocks.TORCH, ModuleNor::new);

		// Effects
		register(Items.CHORUS_FRUIT_POPPED, ModuleBlink::new);
		register(Blocks.TNT, ModuleExplosion::new);
		register(Blocks.HAY_BLOCK, 32, ModuleFallProtection::new);
		register(Items.BLAZE_POWDER, ModuleFlame::new);
		register(Items.LAVA_BUCKET, ModuleLava::new);
		register(Blocks.GLOWSTONE, ModuleLight::new);
		register(new ItemStack(Items.POTIONITEM, 6, OreDictionary.WILDCARD_VALUE), ModulePotion::new);
		register(Items.CAKE, ModuleSaturation::new);
		register(Items.WATER_BUCKET, ModuleWater::new);

		// Events
		register(Items.IRON_SWORD, ModuleMeleeEvent::new);
		register(Items.BOW, ModuleRangedEvent::new);
		register(Items.FISH, ModuleUnderwaterEvent::new);
		register(new ItemStack(Items.FISH, 1, 3), ModuleSuffocationEvent::new);
		register(Items.FEATHER, ModuleFallEvent::new);
		register(Items.FLINT, ModuleOnFireEvent::new);
		register(Items.ENDER_PEARL, ModuleBlinkEvent::new);
		register(Items.GLASS_BOTTLE, ModulePotionEvent::new);

		// Modifiers
		register(new ItemStack(Blocks.WOOL, 16, OreDictionary.WILDCARD_VALUE), ModuleSilent::new);
		register(Blocks.SAND, ModuleDuration::new);
		register(new ItemStack(Items.DYE, 64, 4), ModuleManaCost::new);
		register(Items.SUGAR, 64, ModuleBurnOut::new);
		register(Items.DRAGON_BREATH, ModuleArea::new);
		register(new ItemStack(Blocks.GLASS, 16, OreDictionary.WILDCARD_VALUE), ModulePierce::new);
		register(Items.PRISMARINE_CRYSTALS, ModuleBeamModifier::new);
		register(Items.ARROW, 16, ModuleRangedDamage::new);
		register(Items.SNOWBALL, 100, ModulePunch::new);
		register(Items.SLIME_BALL, 16, ModuleSticky::new);
		register(Blocks.GRAVEL, ModuleScatter::new);
		register(Items.QUARTZ, 16, ModuleProjectileCount::new);
		register(Items.DIAMOND, 3, ModuleMeleeDamage::new);
		register(Items.RABBIT_FOOT, ModuleCritChance::new);
		register(Items.GOLD_INGOT, ModuleMagicDamage::new);
		register(Items.ENCHANTED_BOOK, ModuleEnchantment::new);

		// Shape Modules
		register(Items.PRISMARINE_SHARD, ModuleBeam::new);
		register(Items.BOW, ModuleProjectile::new);
		register(Items.DIAMOND_SWORD, ModuleMelee::new);
		register(Items.GOLDEN_APPLE, ModuleSelf::new);
		register(Blocks.GLASS_PANE, ModuleZone::new);
		register(Items.GUNPOWDER, ModuleCone::new);
	}

	public void register(ItemStack item, IModuleConstructor constructor)
	{
		Module module = constructor.construct();
		String name = module.getClass().getName();
		modules.put(name, constructor);
		switch (module.getType())
		{
			case BOOLEAN:
				booleanItems.put(new StackWrapper(item), name);
				break;
			case EFFECT:
				effectItems.put(new StackWrapper(item), name);
				break;
			case EVENT:
				eventItems.put(new StackWrapper(item), name);
				break;
			case MODIFIER:
				modifierItems.put(new StackWrapper(item), name);
				break;
			case SHAPE:
				shapeItems.put(new StackWrapper(item), name);
				break;
		}
	}
	
	public void register(Block block, IModuleConstructor constructor)
	{
		register(block, 1, constructor);
	}
	
	public void register(Block block, int amount, IModuleConstructor constructor)
	{
		register(new ItemStack(block, amount), constructor);
	}
	
	public void register(Item item, IModuleConstructor constructor)
	{
		register(item, 1, constructor);
	}
	
	public void register(Item item, int amount, IModuleConstructor constructor)
	{
		register(new ItemStack(item, amount), constructor);
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