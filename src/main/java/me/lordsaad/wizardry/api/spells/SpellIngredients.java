package me.lordsaad.wizardry.api.spells;

import me.lordsaad.wizardry.Wizardry;
import me.lordsaad.wizardry.gui.worktable.Module;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Saad on 6/16/2016.
 */
public class SpellIngredients {

    public enum IngredientType {
        SPELLEFFECTMODIFIERS("Spell Effect Modifiers", SpellEffectModifiers.class),
        SPELLSHAPEMODIFIERS("Spell Shape Modifiers", SpellShapeModifiers.class),
        SPELLCONDITIONS("Spell Conditions", SpellConditions.class),
        SPELLEFFECTS("Spell Effects", SpellEffects.class),
        SPELLEVENTS("Spell Events", SpellEvents.class),
        SPELLSHAPES("Spell SHAPES", SpellShapes.class);

        private String name;
        private Class clazz;

        IngredientType(String name, Class clazz) {
            this.name = name;
            this.clazz = clazz;
        }

        public String getName() {
            return name;
        }

        public Class getClazz() {
            return clazz;
        }
    }

    public static class SpellShapes {
        public static final Module BEAM = new Module(new ItemStack(Items.PRISMARINE_SHARD, 1), "The spell will be cast as a continuous beam from the pearl.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
        public static final Module PROJECTILE = new Module(new ItemStack(Items.ARROW, 1), "The spell will be cast as a projectile and do projectile damage.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
        public static final Module MELEE = new Module(new ItemStack(Items.DIAMOND_SWORD, 1), "The spell will be cast when you inflict melee damage with the pearl.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
        public static final Module SELF = new Module(new ItemStack(Items.GOLDEN_APPLE, 1), "The spell will be cast on yourself.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
        public static final Module ZONE = new Module(new ItemStack(Blocks.GLASS_PANE, 1), "The spell will stay in the spot you set and cast the spell.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
    }

    public static class SpellEvents {
        public static final Module ON_MELEE_DAMAGE = new Module(new ItemStack(Items.IRON_SWORD, 1), "If the entity takes melee damage", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
        public static final Module IS_UNDERWATER = new Module(new ItemStack(Items.FISH, 1, 0), "If the entity is underwater", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png")); // regular fish
        public static final Module ON_SUFFOCATION = new Module(new ItemStack(Items.FISH, 1, 3), "If the entity takes any kind of suffocation damage", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png")); // puffer fish
        public static final Module ON_FALL_DAMAGE = new Module(new ItemStack(Items.FEATHER, 1), "If the entity takes fall damage.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
        public static final Module ON_FIRE_DAMAGE = new Module(new ItemStack(Items.FLINT, 1), "If the entity takes fire damage.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
        public static final Module HAS_SPECIFIC_POTION_EFFECT = new Module(new ItemStack(Items.POTIONITEM, 1), "If the entity has a specific potion effect.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
        public static final Module ON_BLINK = new Module(new ItemStack(Items.ENDER_PEARL, 1), "If the entity blinks/teleports.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
        public static final Module ON_PROJECTILE_DAMAGE = new Module(new ItemStack(Items.BOW, 1), "If the entity takes projectile damage.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
    }

    public static class SpellShapeModifiers {
        public static final Module BEAM_EXTEND = new Module(new ItemStack(Items.PRISMARINE_SHARD, 1), "Will extend the beam shape by a block.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
        public static final Module PROJECTILE_POWER_INCREASE = new Module(new ItemStack(Items.ARROW, 16), "Will increase the power the projectile inflicts.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
        public static final Module PROJECTILE_INCREASE_PUNCH = new Module(new ItemStack(Items.SNOWBALL, 100), "Will increase the knockback/punch of the projectile.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
        public static final Module PROJECTILE_STICK = new Module(new ItemStack(Items.SLIME_BALL, 16), "Will make the spell stick to it's target and run continuously.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
        public static final Module PROJECTILE_SCATTER = new Module(new ItemStack(Blocks.GRAVEL, 16), "Will scatter the projectile directions whilst reducing it's potency by the amount of projectiles available.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
        public static final Module PROJECTILE_AMOUNT = new Module(new ItemStack(Items.QUARTZ, 16), "Will increase the amount of projectiles that will be shot per spell cast.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
        public static final Module MELEE_INCREASE_DAMAGE = new Module(new ItemStack(Items.DIAMOND, 3), "Will increase the damage the melee does.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
        public static final Module MELEE_INCREASE_CRIT_CHANCE = new Module(new ItemStack(Items.RABBIT_FOOT, 1), "Will increase the chance of a critical hit.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
        public static final Module MELEE_INCREASE_MAGIC_DAMAGE = new Module(new ItemStack(Items.GOLD_INGOT, 16), "Will increase the magic damage the spell does.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
        public static final Module ZONE_INCREASE_DURATION = new Module(new ItemStack(Blocks.SAND, 1), "Will increase the amount of time the zone will last for.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
        public static final Module ALL_ADD_ENCHANTMENT = new Module(new ItemStack(Items.ENCHANTED_BOOK, 1), "Will add the enchantment to the spell.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
    }

    public static class SpellEffectModifiers {
        public static final Module SILENT = new Module(new ItemStack(Blocks.WOOL, 1), "Will make the spell run silentl.y", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
        public static final Module DECREASE_MANA = new Module(new ItemStack(Items.DYE, 64, 4), "Will decrease the amount of mana the spell uses by 10 points.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png")); // Lapis
        public static final Module DECREASE_BURN_OUT = new Module(new ItemStack(Items.SUGAR, 64), "Will decrease the amount of burnout the spell uses by 10 points.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
        public static final Module INCREASE_DURATION = new Module(new ItemStack(Blocks.SAND, 1), "Will increase the duration of the spell will last for.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
    }

    public static class SpellEffects {
        public static final Module PLACE_LAVA = new Module(new ItemStack(Items.LAVA_BUCKET, 1), "Will place a lava source block at the target", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
        public static final Module PLACE_WATER = new Module(new ItemStack(Items.WATER_BUCKET, 1), "Will place a water source block at the target", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
        public static final Module POTION_ITEM = new Module(new ItemStack(Items.POTIONITEM, 1), "Will use the potion added on the target.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
        public static final Module FIRE_DAMAGE = new Module(new ItemStack(Items.BLAZE_POWDER, 1), "Will inflict fire damage on the target.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
        public static final Module DECREASE_FALL_DAMAGE = new Module(new ItemStack(Blocks.HAY_BLOCK, 32), "Will reduce fall damage by 10%.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
        public static final Module BLINK = new Module(new ItemStack(Items.CHORUS_FRUIT_POPPED, 1), "Will blink the entity to the specified location. If no location is specified, it will teleport in the direction it's facing.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
        public static final Module INCREASE_SATURATION = new Module(new ItemStack(Items.CAKE, 1), "Will increase saturation and food levels by two points each.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
        public static final Module EXPLODE = new Module(new ItemStack(Blocks.TNT, 1), "Will cause an explosion at the target. More than 64 will cause block damage.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
    }

    public static class SpellConditions {
        public static final Module AND = new Module(new ItemStack(Items.STRING, 1, 0), "If X event and Y event both trigger, continue.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
        public static final Module OR = new Module(new ItemStack(Items.WHEAT_SEEDS, 1, 0), "If either X event or Y event trigger, continue.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
        public static final Module NAND = new Module(new ItemStack(Blocks.REDSTONE_TORCH, 1, 0), "If neither X event and Y event trigger, continue.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
        public static final Module NOR = new Module(new ItemStack(Blocks.TORCH, 1, 0), "If either X event doesn't trigger or Y event doesn't trigger, continue.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));
    }
}
