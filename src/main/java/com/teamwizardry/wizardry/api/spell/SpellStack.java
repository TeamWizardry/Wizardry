package com.teamwizardry.wizardry.api.spell;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Created by LordSaad.
 */
public class SpellStack {

    @NotNull
    public Deque<ItemStack> children = new ArrayDeque<>();
    public int depth;

    private IModule head;

    public SpellStack(List<ItemStack> inventory) {
        IModule head = ModuleRegistry.INSTANCE.getModule(inventory.get(0));

        if (head != null && head.getModuleType() == ModuleType.SHAPE) {
            this.head = head;
            Deque<ItemStack> stacks = new ArrayDeque<>(inventory);
            stacks.pop();
            children = stacks;
            depth = 1;
        }
    }

    /**
     * Will start casting the spell from the first shape module.
     *
     * @param world  The world object the spell is to be cast in.
     * @param caster The caster of the spell. May be null.
     * @return Whether the spell succeeded
     */
    public boolean begin(@NotNull World world, @Nullable EntityLivingBase caster) {
        return head.run(world, caster, this);
    }
}
