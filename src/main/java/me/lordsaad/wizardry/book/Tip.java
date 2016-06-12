package me.lordsaad.wizardry.book;

import net.minecraft.item.ItemStack;

import java.util.HashMap;

/**
 * Created by Saad on 6/12/2016.
 */
public class Tip {

    private String text;
    private int ID;
    private float x = 0F;
    private HashMap<Slot, ItemStack> recipe = new HashMap<>();
    private ItemStack recipeOutput;
    private boolean isSlidingOut = true, complete = false;

    public Tip(String text, int ID) {
        this.text = text;
        this.ID = ID;
    }

    public Tip(String text, int ID, ItemStack recipeOutput, HashMap<Slot, ItemStack> recipe) {
        this.text = text;
        this.ID = ID;
        this.recipeOutput = recipeOutput;
        this.recipe = recipe;
    }

    public boolean hasRecipe() {
        return recipeOutput != null;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public HashMap<Slot, ItemStack> getRecipe() {
        return recipe;
    }

    public void setRecipe(HashMap<Slot, ItemStack> recipe) {
        this.recipe = recipe;
    }

    public ItemStack getRecipeOutput() {
        return recipeOutput;
    }

    public void setRecipeOutput(ItemStack recipeOutput) {
        this.recipeOutput = recipeOutput;
    }

    public boolean isSlidingOut() {
        return isSlidingOut;
    }

    public void setSlidingOut(boolean slidingOut) {
        this.isSlidingOut = slidingOut;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
        if (complete) Tippable.deleteTip.add(ID);
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
