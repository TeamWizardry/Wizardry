package me.lordsaad.wizardry.api.spells;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * Created by Saad on 6/16/2016.
 */
public class SpellIngredients {

    public enum IngredientType {
        MODIFIER("Modifiers"),
        CONDITION("Conditions"),
        EFFECT("Effects"),
        PERSPECTIVE("Perspective"),
        EVENT("Event");

        private String name;

        IngredientType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class Modifiers {
        public static final ItemStack BEAM = new ItemStack(Items.PRISMARINE_SHARD, 1);
        public static final ItemStack PROJECTILE = new ItemStack(Items.ARROW, 16);
        public static final ItemStack SILENT = new ItemStack(Blocks.WOOL, 1);
        public static final ItemStack INCREASE_SCATTER = new ItemStack(Blocks.GRAVEL, 16);
        public static final ItemStack INCREASE_DURATION = new ItemStack(Blocks.SAND, 1);
        public static final ItemStack STICK = new ItemStack(Items.SLIME_BALL, 16);
        public static final ItemStack DECREASE_MANA = new ItemStack(Items.DYE, 64, 4); // Lapis
        public static final ItemStack INCREASE_MAGIC_DAMAGE = new ItemStack(Items.GOLD_INGOT, 16);
        public static final ItemStack DECREASE_BURN_OUT = new ItemStack(Items.SUGAR, 64);
    }

    public static class Effects {
        public static final ItemStack PLACE_LAVA = new ItemStack(Items.LAVA_BUCKET, 1);
        public static final ItemStack PLACE_WATER = new ItemStack(Items.WATER_BUCKET, 1);
        public static final ItemStack POTION_SPELL = new ItemStack(Items.NETHER_WART, 1);
        public static final ItemStack POTION_ITEM = new ItemStack(Items.POTIONITEM, 1);
        public static final ItemStack ENCHANTED_BOOK = new ItemStack(Items.ENCHANTED_BOOK, 1);
        public static final ItemStack FIRE_DAMAGE = new ItemStack(Items.BLAZE_POWDER, 1);
        public static final ItemStack INCREASE_SHARPNESS = new ItemStack(Items.DIAMOND, 3);
        public static final ItemStack DECREASE_FALL_DAMAGE = new ItemStack(Blocks.HAY_BLOCK, 32);
        public static final ItemStack BLINK = new ItemStack(Items.CHORUS_FRUIT_POPPED, 1);
        public static final ItemStack INCREASE_SATURATION = new ItemStack(Items.CAKE, 1);
        public static final ItemStack EXPLODE = new ItemStack(Blocks.TNT, 1);
    }

    public static class Events {
        public static final ItemStack ON_PHYSICAL_DAMAGE = new ItemStack(Items.IRON_SWORD, 1);
        public static final ItemStack IS_UNDERWATER = new ItemStack(Items.FISH, 1, 0);
        public static final ItemStack ON_SUFFOCATION = new ItemStack(Items.FISH, 1, 3);
        public static final ItemStack ON_FALL_DAMAGE = new ItemStack(Items.FEATHER, 1);
        public static final ItemStack IS_ON_FIRE = new ItemStack(Items.FLINT, 1);
        public static final ItemStack HAS_SPECIFIC_POTION_EFFECT = new ItemStack(Items.POTIONITEM, 1);
        public static final ItemStack ON_BLINK = new ItemStack(Items.ENDER_PEARL, 1);
    }

    public static class Perspective {
        public static final ItemStack ON_SELF = new ItemStack(Items.GOLDEN_APPLE, 1, 0);
    }

    public static class Conditions {
        public static final ItemStack AND = new ItemStack(Items.STRING, 1, 0);
        public static final ItemStack OR = new ItemStack(Items.WHEAT_SEEDS, 1, 0);
        public static final ItemStack NAND = new ItemStack(Blocks.REDSTONE_TORCH, 1, 0);
        public static final ItemStack NOR = new ItemStack(Blocks.TORCH, 1, 0);
    }
}
