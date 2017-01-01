package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by LordSaad.
 */
public class SpellCluster {

    public static Item[] depthIdentifiers = new Item[]{
            ModItems.FAIRY_DUST,
            ModItems.DEVIL_DUST,
            Items.SPECKLED_MELON,
            ModItems.FAIRY_WINGS,
            Items.BEETROOT,
            Items.SPIDER_EYE,
            Items.PUMPKIN_SEEDS,
            Items.GHAST_TEAR,
            ModItems.FAIRY_IMBUED_APPLE,
            Items.STRING,
            Items.BONE
    };

    Set<SpellStack> spellStacks = new HashSet<>();

    public SpellCluster(ArrayList<ItemStack> inventory) {
        // Split all of level 0.
        // AKA: Splitting the cluster into spell stacks.
        for (List<ItemStack> spellBranch : brancher(inventory, depthIdentifiers[0]))
            spellStacks.add(new SpellStack(spellBranch));
    }

    @NotNull
    public static Set<List<ItemStack>> brancher(List<ItemStack> inventory, Item identifier) {
        Set<List<ItemStack>> branches = new HashSet<>();
        List<ItemStack> temp = new ArrayList<>();
        for (ItemStack stack : inventory) {
            if (ItemStack.areItemsEqual(new ItemStack(identifier), stack)) {
                branches.add(temp);
                temp.clear();
            } else temp.add(stack);
        }
        return branches;
    }
}
