package com.teamwizardry.wizardry.common.module.modifiers;

import com.teamwizardry.wizardry.api.Attributes;
import com.teamwizardry.wizardry.api.spell.IModifier;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Created by LordSaad.
 */
public class ModuleModifierPlus extends Module implements IModifier {

    public ModuleModifierPlus() {
    }

    @NotNull
    @Override
    public ItemStack getRequiredStack() {
        return new ItemStack(Items.GOLD_NUGGET);
    }

    @NotNull
    @Override
    public ModuleType getModuleType() {
        return ModuleType.MODIFIER;
    }

    @NotNull
    @Override
    public String getID() {
        return "modifier_plus";
    }

    @NotNull
    @Override
    public String getReadableName() {
        return "+";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Increase the power of a module hooked";
    }

    @Override
    public double getManaToConsume() {
        return 3;
    }

    @Override
    public double getBurnoutToFill() {
        return 3;
    }

    @Override
    public void apply(Module module) {
        module.attributes.setInteger(Attributes.PLUS, 3);
    }

    @NotNull
    @Override
    public Module copy() {
        ModuleModifierExtend clone = new ModuleModifierExtend();
        clone.children = children;
        return clone;
    }
}
