package com.teamwizardry.wizardry.api.spell;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Created by LordSaad.
 */
public class IModule {

    public Deque<ItemStack> children = new ArrayDeque<>();

    /**
     * The list of modifiers added to this module after the spell is created.
     */
    public Set<IModule> modifierModules = new HashSet<>();

    /**
     * The list of itemstacks that where dumped into the spell after this module
     * and aren't modules themselves are added to this list.
     * <p>
     * A module may interpret these items as they wish.
     */
    public List<ItemStack> extraModifiers = new ArrayList<>();

    public IModule() {
    }

    /**
     * The stack required during crafting to run this module.
     *
     * @return An itemstack representing the price.
     */
    @NotNull
    public ItemStack getRequiredStack() {
        return new ItemStack(Blocks.STONE);
    }

    /**
     * The type of module this module is.
     *
     * @return A ModuleType representing the type of module this is.
     */
    @NotNull
    public ModuleType getModuleType() {
        return ModuleType.SHAPE;
    }

    /**
     * A lower case snake_case string getID that reflects the module to identify it during serialization/deserialization.
     *
     * @return A lower case snake_case string.
     */
    @NotNull
    public String getID() {
        return "null_id";
    }

    /**
     * Represents the readable name of this module. Viewed in the worktable.
     *
     * @return A readable string representing it's name or title.
     */
    @NotNull
    public String getReadableName() {
        return "Null Module";
    }

    /**
     * The description of what this module does.
     *
     * @return A string representing what this module does.
     */
    @NotNull
    public String getDescription() {
        return "This module is null";
    }

    /**
     * A set of modifier modules that this module can be affected by.
     *
     * @return A set of modifier modules;
     */
    @NotNull
    public Set<IModule> getCompatibleModifierModules() {
        return Collections.emptySet();
    }

    /**
     * Run the whatever is required on the SpellStack and then trigger the next step.
     *
     * @param spellStack The SpellStack object holding all the information about the spell.
     * @return whether this spell has succeeded or failed this step.
     */
    public boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @NotNull SpellStack spellStack) {

        return false;
    }

    public ArrayList<IModule> compileNextChildren(@NotNull SpellStack spellStack) {
        ArrayList<IModule> nextModules = new ArrayList<>();

        ArrayList<ItemStack> inventory = new ArrayList<>(children);
        Set<List<ItemStack>> branches = SpellCluster.brancher(inventory, SpellCluster.depthIdentifiers[spellStack.depth]);
        Set<List<ItemStack>> copy = new HashSet<>(branches);

        for (List<ItemStack> branch : copy) {
            Deque<ItemStack> queue = new ArrayDeque<>(branch);
            IModule head = ModuleRegistry.INSTANCE.getModule(queue.peekFirst());
            if (head != null) {
                queue.pop();
                head.children = queue;
                nextModules.add(head);
            }
        }

        spellStack.depth++;

        return nextModules;
    }

    @NotNull
    public IModule copy() {
        return new IModule();
    }
}
