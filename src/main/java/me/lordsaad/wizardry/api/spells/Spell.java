package me.lordsaad.wizardry.api.spells;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Saad on 6/16/2016.
 */
public class Spell {

    private ArrayList<ItemStack> spellModifiers, spellEffects, spellConditions, spellEvents;

    public Spell(HashMap<ItemTypes, ArrayList<ItemStack>> spellIngredients) {
        if (spellIngredients.containsKey(ItemTypes.MODIFIER))
            spellModifiers = spellIngredients.get(ItemTypes.MODIFIER);
        if (spellIngredients.containsKey(ItemTypes.EFFECT))
            spellEffects = spellIngredients.get(ItemTypes.EFFECT);
        if (spellIngredients.containsKey(ItemTypes.CONDITION))
            spellConditions = spellIngredients.get(ItemTypes.CONDITION);
        if (spellIngredients.containsKey(ItemTypes.EVENT))
            spellEffects = spellIngredients.get(ItemTypes.EVENT);
    }

    public ArrayList<ItemStack> getSpellModifiers() {
        return spellModifiers;
    }

    public void setSpellModifiers(ArrayList<ItemStack> spellModifiers) {
        this.spellModifiers = spellModifiers;
    }

    public ArrayList<ItemStack> getSpellEffects() {
        return spellEffects;
    }

    public void setSpellEffects(ArrayList<ItemStack> spellEffects) {
        this.spellEffects = spellEffects;
    }

    public ArrayList<ItemStack> getSpellConditions() {
        return spellConditions;
    }

    public void setSpellConditions(ArrayList<ItemStack> spellConditions) {
        this.spellConditions = spellConditions;
    }

    public ArrayList<ItemStack> getSpellEvents() {
        return spellEvents;
    }

    public void setSpellEvents(ArrayList<ItemStack> spellEvents) {
        this.spellEvents = spellEvents;
    }

    public enum ItemTypes {
        MODIFIER, EFFECT, CONDITION, EVENT
    }
}