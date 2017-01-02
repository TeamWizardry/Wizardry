package com.teamwizardry.wizardry.common.spell.module.modifiers;

import com.teamwizardry.wizardry.api.Attributes;
import com.teamwizardry.wizardry.api.spell.IModifier;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by LordSaad.
 */
public class ModuleModifierExtend extends Module implements IModifier {

    public ModuleModifierExtend() {
    }

    @NotNull
    @Override
    public ItemStack getRequiredStack() {
        return new ItemStack(Items.PRISMARINE_CRYSTALS);
    }

    @NotNull
    @Override
    public ModuleType getModuleType() {
        return ModuleType.MODIFIER;
    }

    @NotNull
    @Override
    public String getID() {
        return "modifier_extend";
    }

    @NotNull
    @Override
    public String getReadableName() {
        return "Extend";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Can increase range or time on shapes and effects.";
    }

    @Override
    public double getManaToConsume() {
        return 0;
    }

    @Override
    public double getBurnoutToFill() {
        return 0;
    }

    @NotNull
    @Override
    public Set<Module> getCompatibleModifierModules() {
        Set<Module> modules = new HashSet<>();
        modules.add(new ModuleModifierPlus());
        return modules;
    }

    @Override
    public void apply(Module module) {
        int power = 2;
        if (attributes.hasKey(Attributes.PLUS)) power *= attributes.getDouble(Attributes.PLUS);
        module.attributes.setDouble(Attributes.EXTEND, power);
    }

    @NotNull
    @Override
    public Module copy() {
        ModuleModifierExtend clone = new ModuleModifierExtend();
        clone.extraModifiers = extraModifiers;
        clone.children = children;
        return clone;
    }
}
