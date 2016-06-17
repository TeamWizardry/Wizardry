package me.lordsaad.wizardry.gui.worktable;

import me.lordsaad.wizardry.Wizardry;
import me.lordsaad.wizardry.api.spells.SpellIngredients;
import net.minecraft.util.ResourceLocation;

import static me.lordsaad.wizardry.api.spells.SpellIngredients.IngredientType.*;

/**
 * Created by Saad on 6/17/2016.
 */
public class Modules {

    public static class Modifiers {
        public static final Module BEAM = new Module(0, 0, new ResourceLocation(Wizardry.MODID, "assets/textures/items/pearl.png"), MODIFIER, SpellIngredients.Modifiers.BEAM);
        public static final Module PROJECTILE = new Module(0, 0, new ResourceLocation(Wizardry.MODID, "assets/textures/items/pearl.png"), MODIFIER, SpellIngredients.Modifiers.PROJECTILE);
        public static final Module INCREASE_SCATTER = new Module(0, 0, new ResourceLocation(Wizardry.MODID, "assets/textures/items/pearl.png"), MODIFIER, SpellIngredients.Modifiers.INCREASE_SCATTER);
        public static final Module INCREASE_DURATION = new Module(0, 0, new ResourceLocation(Wizardry.MODID, "assets/textures/items/pearl.png"), MODIFIER, SpellIngredients.Modifiers.INCREASE_DURATION);
        public static final Module STICK = new Module(0, 0, new ResourceLocation(Wizardry.MODID, "assets/textures/items/pearl.png"), MODIFIER, SpellIngredients.Modifiers.STICK);
        public static final Module DECREASE_MANA = new Module(0, 0, new ResourceLocation(Wizardry.MODID, "assets/textures/items/pearl.png"), MODIFIER, SpellIngredients.Modifiers.DECREASE_MANA);
        public static final Module INCREASE_MAGIC_DAMAGE = new Module(0, 0, new ResourceLocation(Wizardry.MODID, "assets/textures/items/pearl.png"), MODIFIER, SpellIngredients.Modifiers.INCREASE_MAGIC_DAMAGE);
        public static final Module DECREASE_BURNOUT = new Module(0, 0, new ResourceLocation(Wizardry.MODID, "assets/textures/items/pearl.png"), MODIFIER, SpellIngredients.Modifiers.DECREASE_BURN_OUT);
        public static final Module SILENT = new Module(0, 0, new ResourceLocation(Wizardry.MODID, "assets/textures/items/pearl.png"), MODIFIER, SpellIngredients.Modifiers.SILENT);
    }

    public static class Effects {
        public static final Module BEAM = new Module(0, 0, new ResourceLocation(Wizardry.MODID, "assets/textures/items/pearl.png"), EFFECT, SpellIngredients.Effects.PLACE_LAVA);
        public static final Module PLACE_LAVA = new Module(0, 0, new ResourceLocation(Wizardry.MODID, "assets/textures/items/pearl.png"), EFFECT, SpellIngredients.Effects.PLACE_WATER);
        public static final Module PLACE_WATER = new Module(0, 0, new ResourceLocation(Wizardry.MODID, "assets/textures/items/pearl.png"), EFFECT, SpellIngredients.Effects.POTION_SPELL);
        public static final Module POTION_SPELL = new Module(0, 0, new ResourceLocation(Wizardry.MODID, "assets/textures/items/pearl.png"), EFFECT, SpellIngredients.Effects.POTION_ITEM);
        public static final Module POTION_ITEM = new Module(0, 0, new ResourceLocation(Wizardry.MODID, "assets/textures/items/pearl.png"), EFFECT, SpellIngredients.Effects.ENCHANTED_BOOK);
        public static final Module ENCHANTED_BOOK = new Module(0, 0, new ResourceLocation(Wizardry.MODID, "assets/textures/items/pearl.png"), EFFECT, SpellIngredients.Effects.FIRE_DAMAGE);
        public static final Module FIRE_DAMAGE = new Module(0, 0, new ResourceLocation(Wizardry.MODID, "assets/textures/items/pearl.png"), EFFECT, SpellIngredients.Effects.INCREASE_SHARPNESS);
        public static final Module INCREASE_SHARPNESS = new Module(0, 0, new ResourceLocation(Wizardry.MODID, "assets/textures/items/pearl.png"), EFFECT, SpellIngredients.Effects.DECREASE_FALL_DAMAGE);
        public static final Module DECREASE_FALL_DAMAGE = new Module(0, 0, new ResourceLocation(Wizardry.MODID, "assets/textures/items/pearl.png"), EFFECT, SpellIngredients.Effects.BLINK);
        public static final Module BLINK = new Module(0, 0, new ResourceLocation(Wizardry.MODID, "assets/textures/items/pearl.png"), EFFECT, SpellIngredients.Effects.INCREASE_SATURATION);
        public static final Module INCREASE_SATURATION = new Module(0, 0, new ResourceLocation(Wizardry.MODID, "assets/textures/items/pearl.png"), EFFECT, SpellIngredients.Effects.EXPLODE);
    }

    public static class Events {
        public static final Module ON_PHYSICAL_DAMAGE = new Module(0, 0, new ResourceLocation(Wizardry.MODID, "assets/textures/items/pearl.png"), EVENT, SpellIngredients.Events.ON_PHYSICAL_DAMAGE);
        public static final Module IS_UNDERWATER = new Module(0, 0, new ResourceLocation(Wizardry.MODID, "assets/textures/items/pearl.png"), EVENT, SpellIngredients.Events.IS_UNDERWATER);
        public static final Module ON_SUFFOCATION = new Module(0, 0, new ResourceLocation(Wizardry.MODID, "assets/textures/items/pearl.png"), EVENT, SpellIngredients.Events.ON_SUFFOCATION);
        public static final Module ON_FALL_DAMAGE = new Module(0, 0, new ResourceLocation(Wizardry.MODID, "assets/textures/items/pearl.png"), EVENT, SpellIngredients.Events.ON_FALL_DAMAGE);
        public static final Module IS_ON_FIRE = new Module(0, 0, new ResourceLocation(Wizardry.MODID, "assets/textures/items/pearl.png"), EVENT, SpellIngredients.Events.IS_ON_FIRE);
        public static final Module HAS_SPECIFIC_POTION_EFFECT = new Module(0, 0, new ResourceLocation(Wizardry.MODID, "assets/textures/items/pearl.png"), EVENT, SpellIngredients.Events.HAS_SPECIFIC_POTION_EFFECT);
        public static final Module ON_BLINK = new Module(0, 0, new ResourceLocation(Wizardry.MODID, "assets/textures/items/pearl.png"), EVENT, SpellIngredients.Events.ON_BLINK);
    }

    public static class Perspective {
        public static final Module ON_SELF = new Module(0, 0, new ResourceLocation(Wizardry.MODID, "assets/textures/items/pearl.png"), PERSPECTIVE, SpellIngredients.Perspective.ON_SELF);
    }

    public static class CONDITION {
        public static final Module AND = new Module(0, 0, new ResourceLocation(Wizardry.MODID, "assets/textures/items/pearl.png"), CONDITION, SpellIngredients.Conditions.AND);
        public static final Module OR = new Module(0, 0, new ResourceLocation(Wizardry.MODID, "assets/textures/items/pearl.png"), CONDITION, SpellIngredients.Conditions.OR);
        public static final Module NAND = new Module(0, 0, new ResourceLocation(Wizardry.MODID, "assets/textures/items/pearl.png"), CONDITION, SpellIngredients.Conditions.NAND);
        public static final Module NOR = new Module(0, 0, new ResourceLocation(Wizardry.MODID, "assets/textures/items/pearl.png"), CONDITION, SpellIngredients.Conditions.NOR);

    }
}
