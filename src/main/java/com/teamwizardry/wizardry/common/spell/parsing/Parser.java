package com.teamwizardry.wizardry.common.spell.parsing;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javafx.collections.transformation.SortedList;
import net.minecraft.item.ItemStack;
import com.google.common.collect.Maps;
import com.sun.javafx.collections.ObservableListWrapper;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.IRequireItem;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.init.ModItems;

public class Parser {

    private Deque<ItemStack> stacks;
    private int endCount = 0;

    public Parser(List<ItemStack> items) {
        stacks = new ArrayDeque<>(items);
    }

    public static HashMap<ModuleType, SortedList<Module>> parseModuleToLists(Module module) {
        HashMap<ModuleType, SortedList<Module>> lists = Maps.newHashMap();
        for (ModuleType type : ModuleType.values()) lists.putIfAbsent(type, new SortedList<>(new ObservableListWrapper<>(new LinkedList<>())));

        List<Module> children = module.getChildren();
        for (Module child : children) {
            if (child.hasChildren()) {
                lists.putAll(parseModuleToLists(child));
            }
            lists.get(child.getType()).add(child);
        }
        return lists;
    }

    /**
     * Will convert the list of items into a Module with all the respective children.
     *
     * @return A module with children that was translated from the inventory provided
     */
    public Module parseInventoryToModule() {
        Module module = getModuleFromItemStack(stacks.pop());
        if (module == null) return null;
        ModuleType currentType = module.getType();
        ModuleType expectedType = getDefaultType(currentType);
        if (module.canHaveChildren()) {
            while (endCount == 0 && expectedType != null) {
                if (stacks.isEmpty())
                    return module;
                endCount = getEndCount(stacks.peek());
                if (endCount != 0) {
                    while (endCount != 0 && expectedType != null) {
                        expectedType = getNextType(currentType, expectedType);
                        endCount--;
                    }
                    if (endCount != 0) {
                        stacks.pop();
                        break;
                    }
                }

                if (expectedType == null)
                    return module;
                Module childModule = parseInventoryToModule();
                if (childModule != null)
                    module.accept(childModule);
            }
            endCount--;
        }
        return module;
    }

    /**
     * Gets a new instance of the module given an item
     */
    private Module getModuleFromItemStack(ItemStack stack) {
        Module module = ModuleRegistry.getInstance().getModuleFromItemStack(stack);
        if (module instanceof IRequireItem) ((IRequireItem) module).handle(stack);
        return module;
    }

    /**
     * Gets the number of levels to end given an item. 0 if the item isn't an
     * end item
     */
    private int getEndCount(ItemStack stack) {
        if (stack == null) return 0;
        return stack.getItem() == ModItems.DEVIL_DUST ? 1 : 0;
    }

    private ModuleType getNextType(ModuleType currentType, ModuleType expectedType) {
        switch (currentType) {
            case MODIFIER:
            case EFFECT:
                return null;
            case SHAPE:
                if (expectedType == ModuleType.MODIFIER)
                    return ModuleType.BOOLEAN;
            case BOOLEAN:
                if (expectedType == ModuleType.BOOLEAN)
                    return ModuleType.EVENT;
                if (expectedType == ModuleType.EVENT)
                    return ModuleType.EFFECT;
            case EVENT:
                if (expectedType == ModuleType.EFFECT)
                    return ModuleType.SHAPE;
                if (expectedType == ModuleType.SHAPE)
                    return null;
            default:
                return null;
        }
    }

    private ModuleType getDefaultType(ModuleType currentType) {
        switch (currentType) {
            case MODIFIER:
                return ModuleType.MODIFIER;
            case EFFECT:
                return ModuleType.MODIFIER;
            case EVENT:
                return ModuleType.SHAPE;
            case SHAPE:
                return ModuleType.MODIFIER;
            case BOOLEAN:
                return ModuleType.BOOLEAN;
            default:
                return null;
        }
    }
}
