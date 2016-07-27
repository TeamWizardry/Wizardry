package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.ModuleRegistry;
import com.teamwizardry.wizardry.common.spell.module.booleans.*;
import com.teamwizardry.wizardry.common.spell.module.effects.*;
import com.teamwizardry.wizardry.common.spell.module.events.*;
import com.teamwizardry.wizardry.common.spell.module.modifiers.*;
import com.teamwizardry.wizardry.common.spell.module.shapes.*;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

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
        ModuleRegistry.Pair<Integer, Module> pair = ModuleRegistry.getInstance().registerModule(constructor, a);
        pair.v.setId(pair.t);

    }

}
