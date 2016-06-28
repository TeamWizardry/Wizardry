package com.teamwizardry.wizardry.api.recipe;

import com.teamwizardry.wizardry.common.item.ItemRing;
import com.teamwizardry.wizardry.common.item.pearl.ItemQuartzPearl;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nullable;

/**
 * Created by Saad on 6/13/2016.
 */
public class RingRecipe implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        boolean foundRing = false;
        boolean foundPearl = false;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null) {
                if (stack.getItem() instanceof ItemRing && stack.getItemDamage() == 0)
                    foundRing = true;

                else if (stack.getItem() instanceof ItemQuartzPearl)
                    foundPearl = true;
                else return false;
            }
        }
        return foundRing && foundPearl;
    }

    @Nullable
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack pearl = null;
        ItemStack ring = null;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null) {
                if (stack.getItem() instanceof ItemRing && stack.getItemDamage() == 0)
                    ring = stack;
                else if (stack.getItem() instanceof ItemQuartzPearl)
                    pearl = stack;
            }
        }

        if (pearl == null || ring == null)
            return null;

        ItemStack ringCopy = ring.copy();
        ringCopy.setItemDamage(1);
        if (pearl.hasTagCompound()) ringCopy.setTagCompound(pearl.getTagCompound());

        return ringCopy;
    }

    @Override
    public int getRecipeSize() {
        return 10;
    }

    @Nullable
    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv) {
        return ForgeHooks.defaultRecipeGetRemainingItems(inv);
    }
}
