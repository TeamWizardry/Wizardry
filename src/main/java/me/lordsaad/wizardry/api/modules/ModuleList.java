package me.lordsaad.wizardry.api.modules;

import me.lordsaad.wizardry.spells.modules.booleans.*;
import me.lordsaad.wizardry.spells.modules.effects.*;
import me.lordsaad.wizardry.spells.modules.events.*;
import me.lordsaad.wizardry.spells.modules.modifiers.*;
import me.lordsaad.wizardry.spells.modules.shapes.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import com.google.common.collect.HashBiMap;

/**
 * @author murapix
 *         <p>
 *         Created on June 21, 2016
 */
public class ModuleList
{
	public HashBiMap<Module, ItemStack> moduleItems;

	public void init()
	{
		moduleItems = HashBiMap.create();
		
		// Boolean Modules
		moduleItems.put(new ModuleAnd(), new ItemStack(Items.STRING));
		moduleItems.put(new ModuleOr(), new ItemStack(Items.WHEAT_SEEDS));
		moduleItems.put(new ModuleNand(), new ItemStack(Blocks.REDSTONE_TORCH));
		moduleItems.put(new ModuleNor(), new ItemStack(Blocks.TORCH));

		// Effect Modules
		moduleItems.put(new ModuleBlink(), new ItemStack(Items.CHORUS_FRUIT_POPPED));
		moduleItems.put(new ModuleExplosion(), new ItemStack(Blocks.TNT));
		moduleItems.put(new ModuleFallProtection(), new ItemStack(Blocks.HAY_BLOCK, 32));
		moduleItems.put(new ModuleFlame(), new ItemStack(Items.BLAZE_POWDER));
		moduleItems.put(new ModuleLava(), new ItemStack(Items.LAVA_BUCKET));
		moduleItems.put(new ModuleLight(), new ItemStack(Blocks.GLOWSTONE));
		moduleItems.put(new ModulePotion(), new ItemStack(Items.POTIONITEM, 6, OreDictionary.WILDCARD_VALUE));
		moduleItems.put(new ModuleSaturation(), new ItemStack(Blocks.CAKE));
		moduleItems.put(new ModuleWater(), new ItemStack(Items.WATER_BUCKET));

		// Event Modules
		moduleItems.put(new ModuleMeleeEvent(), new ItemStack(Items.IRON_SWORD));
		moduleItems.put(new ModuleRangedEvent(), new ItemStack(Items.BOW));
		moduleItems.put(new ModuleUnderwaterEvent(), new ItemStack(Items.FISH));
		moduleItems.put(new ModuleSuffocationEvent(), new ItemStack(Items.FISH, 1, 3));
		moduleItems.put(new ModuleFallEvent(), new ItemStack(Items.FEATHER));
		moduleItems.put(new ModuleOnFireEvent(), new ItemStack(Items.FLINT));
		moduleItems.put(new ModuleBlinkEvent(), new ItemStack(Items.ENDER_PEARL));
		moduleItems.put(new ModulePotionEvent(), new ItemStack(Items.GLASS_BOTTLE, 1));

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
}