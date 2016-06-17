package me.lordsaad.wizardry.gui.worktable;

import me.lordsaad.wizardry.api.spells.SpellIngredients;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Saad on 6/17/2016.
 */
public class Module {

    private int x, y;
    private ResourceLocation icon;
    private SpellIngredients.IngredientType type;

    public Module(int x, int y, ResourceLocation icon, SpellIngredients.IngredientType type) {
        this.x = x;
        this.y = y;
        this.icon = icon;
        this.type = type;
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

    public SpellIngredients.IngredientType getType() {
        return type;
    }

    public void setType(SpellIngredients.IngredientType type) {
        this.type = type;
    }
}
