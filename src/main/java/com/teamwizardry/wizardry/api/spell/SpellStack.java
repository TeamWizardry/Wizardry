package com.teamwizardry.wizardry.api.spell;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Created by LordSaad.
 */
public class SpellStack {

    @NotNull
    public Deque<ItemStack> children = new ArrayDeque<>();
    public int depth, width, maxWidth = 30, maxHeight = 30;

    public Module head;

    public Module[][] grid = new Module[maxWidth][maxHeight];

    public SpellStack(List<ItemStack> inventory) {
        Module head = ModuleRegistry.INSTANCE.getModule(inventory.get(0));

        if (head != null && head.getModuleType() == ModuleType.SHAPE) {
            this.head = head;

            for (ItemStack stack : inventory) {
                Module module = ModuleRegistry.INSTANCE.getModule(stack);
                if (module == null) {
                    depth++;
                    width = 0;
                } else {
                    grid[depth][width] = module;
                    module.depth = depth;
                    module.width = width;
                    width++;
                }
            }


            Deque<ItemStack> stacks = new ArrayDeque<>(inventory);
            stacks.pop();
            children = stacks;
            depth = 1;
        }

        depth = 0;
        width = 0;
    }
}
