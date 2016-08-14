package com.teamwizardry.wizardry.init;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import com.teamwizardry.wizardry.api.module.ModuleRegistry;
import com.teamwizardry.wizardry.common.spell.module.booleans.ModuleAnd;
import com.teamwizardry.wizardry.common.spell.module.booleans.ModuleNand;
import com.teamwizardry.wizardry.common.spell.module.booleans.ModuleNor;
import com.teamwizardry.wizardry.common.spell.module.booleans.ModuleNot;
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
import com.teamwizardry.wizardry.common.spell.module.modifiers.ModuleCritChance;
import com.teamwizardry.wizardry.common.spell.module.modifiers.ModuleDuration;
import com.teamwizardry.wizardry.common.spell.module.modifiers.ModuleEnchantment;
import com.teamwizardry.wizardry.common.spell.module.modifiers.ModuleMagicDamage;
import com.teamwizardry.wizardry.common.spell.module.modifiers.ModuleMeleeDamage;
import com.teamwizardry.wizardry.common.spell.module.modifiers.ModulePierce;
import com.teamwizardry.wizardry.common.spell.module.modifiers.ModulePower;
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

//modmod, hehe
public class ModModules {

    public static void init() {
        // Booleans
        register(Items.STRING, ModuleAnd::new);
        register(Items.WHEAT_SEEDS, ModuleOr::new);
        register(Blocks.REDSTONE_TORCH, ModuleNand::new);
        register(Blocks.TORCH, ModuleNor::new);
        register(Items.REDSTONE, ModuleNot::new);

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
        register(new ItemStack(Items.POTIONITEM, 3, OreDictionary.WILDCARD_VALUE), ModulePotionEvent::new);

        // Modifiers
        register(new ItemStack(Blocks.WOOL, 16, OreDictionary.WILDCARD_VALUE), ModuleSilent::new);
        register(Blocks.SAND, ModuleDuration::new);
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
        register(Items.GLOWSTONE_DUST, 16, ModulePower::new);

        // Shape Modules
        register(Items.PRISMARINE_SHARD, ModuleBeam::new);
        register(Items.BOW, ModuleProjectile::new);
        register(Items.DIAMOND_SWORD, ModuleMelee::new);
        register(Items.GOLDEN_APPLE, ModuleSelf::new);
        register(Blocks.GLASS_PANE, ModuleZone::new);
        register(Items.GUNPOWDER, ModuleCone::new);
    }

    private static void register(Block a, int amount, ModuleRegistry.IModuleConstructor constructor) {
        register(new ItemStack(a, amount), constructor);
    }
    private static void register(Item a, int amount, ModuleRegistry.IModuleConstructor constructor) {
        register(new ItemStack(a, amount), constructor);
    }

    private static void register(Item a, ModuleRegistry.IModuleConstructor constructor) {
        register(new ItemStack(a, 1), constructor);

    }
    private static void register(Block a, ModuleRegistry.IModuleConstructor constructor) {
        register(new ItemStack(a, 1), constructor);
    }
    private static void register(ItemStack a, ModuleRegistry.IModuleConstructor constructor) {
        ModuleRegistry.getInstance().registerModule(constructor, a);
    }

}
