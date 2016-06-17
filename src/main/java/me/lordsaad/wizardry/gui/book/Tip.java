package me.lordsaad.wizardry.gui.book;

import net.minecraft.item.ItemStack;

import java.util.HashMap;

/**
 * Created by Saad on 6/12/2016.
 */
public class Tip {

    private String text;
    private float x = 0F, y = PageBase.top + 10;
    private HashMap<Slot, ItemStack> recipe = new HashMap<>();
    private ItemStack recipeOutput;
    private boolean isSlidingOut = true, complete = false;

    public Tip(String text) {
        this.text = text;
    }

    public Tip(String text, float y) {
        this.text = text;
        this.y = y;
    }

    public Tip(String text, ItemStack recipeOutput, HashMap<Slot, ItemStack> recipe) {
        this.text = text;
        this.recipeOutput = recipeOutput;
        this.recipe = recipe;
    }

    public Tip(String text, float y, ItemStack recipeOutput, HashMap<Slot, ItemStack> recipe) {
        this.text = text;
        this.recipeOutput = recipeOutput;
        this.recipe = recipe;
        this.y = y;
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

    public void setComplete(boolean complete, int ID) {
        this.complete = complete;
        if (complete) Tippable.deleteTip.add(ID);
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
