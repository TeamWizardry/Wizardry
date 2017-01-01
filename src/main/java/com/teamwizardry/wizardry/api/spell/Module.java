package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.wizardry.api.capability.IWizardryCapability;
import com.teamwizardry.wizardry.api.capability.WizardryCapabilityProvider;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Created by LordSaad.
 */
public class Module {

    public int depth, width;

    /**
     * Extra information that can be editted and read by the module.
     * Used by modifiers.
     */
    public NBTTagCompound attributes = new NBTTagCompound();

    /**
     * The branches under this module in the stream of stacks provided in the recipe.
     */
    public Deque<ItemStack> children = new ArrayDeque<>();

    /**
     * The list of modifiers added to this module after the spell is created.
     */
    public Set<Module> modifierModules = new HashSet<>();

    /**
     * The list of itemstacks that where dumped into the spell after this module
     * and aren't modules themselves are added to this list.
     * <p>
     * A module may interpret these items as they wish.
     */
    public List<ItemStack> extraModifiers = new ArrayList<>();

    public Module() {
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
    public Set<Module> getCompatibleModifierModules() {
        return Collections.emptySet();
    }

    public double getManaToConsume() {
        return 10;
    }

    public double getBurnoutToFill() {
        return 10;
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

    public ArrayList<Module> compileNextChildren(@NotNull SpellStack spellStack) {
        ArrayList<Module> nextModules = new ArrayList<>();

        ArrayList<ItemStack> inventory = new ArrayList<>(children);
        Set<List<ItemStack>> branches = SpellCluster.brancher(inventory, SpellCluster.depthIdentifiers[spellStack.depth]);
        Set<List<ItemStack>> copy = new HashSet<>(branches);

        for (List<ItemStack> branch : copy) {
            Deque<ItemStack> queue = new ArrayDeque<>(branch);
            Module head = ModuleRegistry.INSTANCE.getModule(queue.peekFirst());
            if (head != null) {
                queue.pop();
                head.children = queue;
                nextModules.add(head);
            }
        }

        spellStack.depth++;

        return nextModules;
    }

    @Nullable
    public IWizardryCapability getCap(EntityLivingBase entity) {
        if (entity != null && entity instanceof EntityPlayer)
            return WizardryCapabilityProvider.get((EntityPlayer) entity);
        return null;
    }

    public boolean canAcceptModifier(Module module) {
        boolean flag = false;
        for (Module comp : getCompatibleModifierModules())
            if (comp.getClass().isAssignableFrom(module.getClass())) {
                flag = true;
                break;
            }
        return flag;
    }

    /**
     * Basically: http://i.imgur.com/hEdlCzt.png
     * Apply modifiers that can be applied to modifiers.
     * Then apply everything left to cell width [0].
     *
     * @param spellStack The spellStack
     */
    public void applyModifiers(SpellStack spellStack) {
        Module[][] grid = spellStack.grid;
        List<Module> finalModifiers = new ArrayList<>();

        Set<Integer> skipCells = new HashSet<>();
        if (grid[spellStack.depth][spellStack.width + 1] != null) {
            for (int i = 0; i < spellStack.maxWidth - spellStack.width; i++) {
                if (skipCells.contains(spellStack.width + i)) continue;

                Module mainModifier = grid[spellStack.depth][spellStack.width + i];
                if (mainModifier == null) break;
                if (!(mainModifier instanceof IModifier)) break;


                Set<Integer> modifierModifiers = getModifierModifers(spellStack, spellStack.depth, spellStack.width + i);
                skipCells.addAll(modifierModifiers);

                for (int j : modifierModifiers) {
                    Module modifierModifier = grid[spellStack.depth][j];
                    if (mainModifier.canAcceptModifier(modifierModifier) && modifierModifier instanceof IModifier)
                        ((IModifier) modifierModifier).apply(mainModifier, spellStack);
                }

                finalModifiers.add(mainModifier);
            }
        }

        Module mainModule = spellStack.grid[spellStack.depth][0];
        if (mainModule != null)
            for (Module modifier : finalModifiers) {
                if (!(modifier instanceof IModifier)) break;
                ((IModifier) modifier).apply(mainModule, spellStack);
            }
    }

    /**
     * Example: A PLUS modifier modifying EXTEND to power it further.
     *
     * @param spellStack The spellStack.
     * @param depthPos   The current depth position of, in this example, "EXTEND"
     * @param widthPos   The current width position of, in this example, "EXTEND"
     * @return A set of ints representing the horizontal cells containing,
     * in this example, "PLUS" cells, that modify, in this example, "EXTEND"
     */
    private Set<Integer> getModifierModifers(SpellStack spellStack, int depthPos, int widthPos) {
        Module mainModifier = spellStack.grid[depthPos][widthPos];
        Set<Integer> modifyingModifiers = new HashSet<>();
        for (int i = 0; i < spellStack.maxWidth - widthPos; i++) {
            Module modifier = spellStack.grid[depthPos][widthPos + i];
            if (!(modifier instanceof IModifier)) break;
            if (mainModifier.canAcceptModifier(modifier)) {
                modifyingModifiers.add(widthPos + i);
            } else break;
        }
        return modifyingModifiers;
    }

    @NotNull
    public Module copy() {
        return new Module();
    }
}
