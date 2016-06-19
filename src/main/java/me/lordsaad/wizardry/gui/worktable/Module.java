package me.lordsaad.wizardry.gui.worktable;

import me.lordsaad.wizardry.api.spells.SpellIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;

/**
 * Created by Saad on 6/17/2016.
 */
public class Module {

    private int x, y, ID;
    private ResourceLocation icon;
    private SpellIngredients.IngredientType type;
    private ItemStack stack;
    private String text;
    private ArrayList<Module> modules;

    public Module(String text, ResourceLocation icon, SpellIngredients.IngredientType type, ItemStack stack) {
        this.icon = icon;
        this.type = type;
        this.stack = stack;
        this.text = text;
        modules = new ArrayList<>();
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
        Module module = new Module(text, icon, type, stack);
        module.setID(ID);
        module.setX(x);
        module.setY(y);
        module.setModules(modules);
        return module;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public ArrayList<Module> getModules() {
        return modules;
    }

    public void setModules(ArrayList<Module> modules) {
        this.modules = modules;
    }

    public void clearModules() {
        if (!modules.isEmpty()) {

            ArrayList<Module> concurrentModules = new ArrayList<>();
            concurrentModules.addAll(modules); // original
            for (Module linkedModule : concurrentModules) { // linked to original

                ArrayList<Module> doubleConcurrentModules = new ArrayList<>();
                doubleConcurrentModules.addAll(linkedModule.getModules());
                for (Module doubleLinkedModule : linkedModule.getModules())
                    if (doubleLinkedModule.getID() == ID) {
                        doubleConcurrentModules.remove(this);
                    }
                linkedModule.setModules(doubleConcurrentModules);
            }
        }
    }
}
