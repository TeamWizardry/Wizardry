package me.lordsaad.wizardry.gui.worktable;

import me.lordsaad.wizardry.Wizardry;
import me.lordsaad.wizardry.api.spells.SpellIngredients;
import net.minecraft.util.ResourceLocation;

import static me.lordsaad.wizardry.api.spells.SpellIngredients.IngredientType.*;

/**
 * Created by Saad on 6/17/2016.
 */
public class Modules {

    private static int ID = 0;

    public static class Modifiers {
        public static final Module BEAM = new Module(ID++, 0, 0, "The spell will be shot as a beam from the pearl.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"), MODIFIER, SpellIngredients.Modifiers.BEAM);
        public static final Module PROJECTILE = new Module(ID++, 0, 0, "The spell will be shot as a projectile like an arrow.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"), MODIFIER, SpellIngredients.Modifiers.PROJECTILE);
        public static final Module INCREASE_SCATTER = new Module(ID++, 0, 0, "The spell will be shot outwards in small less potent quantities in different directions.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"), MODIFIER, SpellIngredients.Modifiers.INCREASE_SCATTER);
        public static final Module INCREASE_DURATION = new Module(ID++, 0, 0, "The effect of the spell will last longer.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"), MODIFIER, SpellIngredients.Modifiers.INCREASE_DURATION);
        public static final Module STICK = new Module(ID++, 0, 0, "The spell will stick to the particles/block it comes in contact with.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"), MODIFIER, SpellIngredients.Modifiers.STICK);
        public static final Module DECREASE_MANA = new Module(ID++, 0, 0, "Will reduce the amount of mana required to cast the spell.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"), MODIFIER, SpellIngredients.Modifiers.DECREASE_MANA);
        public static final Module INCREASE_MAGIC_DAMAGE = new Module(ID++, 0, 0, "Will increase the magic damage of the spell. Aka: Armor Piercing", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"), MODIFIER, SpellIngredients.Modifiers.INCREASE_MAGIC_DAMAGE);
        public static final Module DECREASE_BURNOUT = new Module(ID++, 0, 0, "Will reduce the amount of burnout as an after effect of casting the spell.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"), MODIFIER, SpellIngredients.Modifiers.DECREASE_BURN_OUT);
        public static final Module SILENT = new Module(ID++, 0, 0, "Will mute the spell. No sounds will be emitted from it.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"), MODIFIER, SpellIngredients.Modifiers.SILENT);
        public static final Module ENCHANTED_BOOK = new Module(ID++, 0, 0, "Will apply the enchantment to the spell.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"), MODIFIER, SpellIngredients.Effects.ENCHANTED_BOOK);
        public static final Module INCREASE_SHARPNESS = new Module(ID++, 0, 0, "Will apply more damage to the target.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"), MODIFIER, SpellIngredients.Effects.INCREASE_SHARPNESS);
    }

    public static class Effects {
        public static final Module PLACE_LAVA = new Module(ID++, 0, 0, "Will place a lava source block at the position of the spell. Will stay permanently if enough duration modifiers are added.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"), EFFECT, SpellIngredients.Effects.PLACE_LAVA);
        public static final Module PLACE_WATER = new Module(ID++, 0, 0, "Will place a water source block at the position of the spell. Will stay permanently if enough duration modifiers are added.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"), EFFECT, SpellIngredients.Effects.PLACE_WATER);
        public static final Module POTION_SPELL = new Module(ID++, 0, 0, "Will enable the spell to be crafted with a potion.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"), EFFECT, SpellIngredients.Effects.POTION_ITEM);
        public static final Module POTION_ITEM = new Module(ID++, 0, 0, "Will inflict the potion effect on the target.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"), EFFECT, SpellIngredients.Effects.POTION_SPELL);
        public static final Module FIRE_DAMAGE = new Module(ID++, 0, 0, "Will set the target on fire for 1 tick. Duration modifiers will increase the amount of ticks.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"), EFFECT, SpellIngredients.Effects.FIRE_DAMAGE);
        public static final Module DECREASE_FALL_DAMAGE = new Module(ID++, 0, 0, "Will reduce the amount of fall damage you take by 10%. More increase percentage.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"), EFFECT, SpellIngredients.Effects.DECREASE_FALL_DAMAGE);
        public static final Module BLINK = new Module(ID++, 0, 0, "Will teleport you 1 block in the direction you're facing. More will increase the amount of blocks you can blink.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"), EFFECT, SpellIngredients.Effects.BLINK);
        public static final Module INCREASE_SATURATION = new Module(ID++, 0, 0, "Will fill you're saturation and hunger levels by 2 packets each. More will replenish your hunger even further.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"), EFFECT, SpellIngredients.Effects.INCREASE_SATURATION);
        public static final Module EXPLODE = new Module(ID++, 0, 0, "Will create an explosion that will not harm terrain unless you add more tnt (x64)", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"), EFFECT, SpellIngredients.Effects.EXPLODE);
    }

    public static class Events {
        public static final Module ON_PHYSICAL_DAMAGE = new Module(ID++, 0, 0, "If the player takes any physical hits like from a melee weapon.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"), EVENT, SpellIngredients.Events.ON_PHYSICAL_DAMAGE);
        public static final Module IS_UNDERWATER = new Module(ID++, 0, 0, "If the player is under water.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"), EVENT, SpellIngredients.Events.IS_UNDERWATER);
        public static final Module ON_SUFFOCATION = new Module(ID++, 0, 0, "If the player is suffocating.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"), EVENT, SpellIngredients.Events.ON_SUFFOCATION);
        public static final Module ON_FALL_DAMAGE = new Module(ID++, 0, 0, "If the player took fall damage.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"), EVENT, SpellIngredients.Events.ON_FALL_DAMAGE);
        public static final Module IS_ON_FIRE = new Module(ID++, 0, 0, "If the player is on fire.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"), EVENT, SpellIngredients.Events.IS_ON_FIRE);
        public static final Module HAS_SPECIFIC_POTION_EFFECT = new Module(ID++, 0, 0, "If the player has a specific active potion effect.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"), EVENT, SpellIngredients.Events.HAS_SPECIFIC_POTION_EFFECT);
        public static final Module ON_BLINK = new Module(ID++, 0, 0, "If the player blinks.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"), EVENT, SpellIngredients.Events.ON_BLINK);
    }

    public static class Perspective {
        public static final Module ON_SELF = new Module(ID++, 0, 0, "Will apply the spell on your character.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"), PERSPECTIVE, SpellIngredients.Perspective.ON_SELF);
    }

    public static class CONDITION {
        public static final Module AND = new Module(ID++, 0, 0, "If two or more events all succeed.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"), CONDITION, SpellIngredients.Conditions.AND);
        public static final Module OR = new Module(ID++, 0, 0, "If at least one of two or more events succeeds.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"), CONDITION, SpellIngredients.Conditions.OR);
        public static final Module NAND = new Module(ID++, 0, 0, "If all events do not succeed.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"), CONDITION, SpellIngredients.Conditions.NAND);
        public static final Module NOR = new Module(ID++, 0, 0, "If at least one of two or more events do not succeed.", new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"), CONDITION, SpellIngredients.Conditions.NOR);

    }
}
