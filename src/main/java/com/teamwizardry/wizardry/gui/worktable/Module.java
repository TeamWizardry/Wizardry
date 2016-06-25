package com.teamwizardry.wizardry.gui.worktable;

import com.teamwizardry.wizardry.api.spells.SpellIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Saad on 6/17/2016.
 */
public class Module {

    // TODO: DELETE ENTIRE CLASS

    private int x, y, ID;
    private ResourceLocation icon;
    private ItemStack stack;
    private String text;
    private SpellIngredients.IngredientType type;

    public Module(ItemStack stack, String text, ResourceLocation icon) {
        this.icon = icon;
        this.stack = stack;
        this.text = text;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public ResourceLocation getIcon() {
        return icon;
    }

    public void setIcon(ResourceLocation icon) {
        this.icon = icon;
    }

    public ItemStack getStack() {
        return stack;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Module copy() {
        Module module = new Module(stack, text, icon);
        module.setX(x);
        module.setY(y);
        module.setType(type);
        return module;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public SpellIngredients.IngredientType getType() {
        return type;
    }

    public void setType(SpellIngredients.IngredientType type) {
        this.type = type;
    }
}
