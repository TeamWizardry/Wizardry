package com.teamwizardry.wizardry.common.module.events;

import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.SpellStack;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Created by LordSaad.
 */
public class ModuleEventAlongPath extends Module {

    public ModuleEventAlongPath() {
    }

    @NotNull
    @Override
    public ItemStack getRequiredStack() {
        return new ItemStack(Items.FEATHER);
    }

    @NotNull
    @Override
    public ModuleType getModuleType() {
        return ModuleType.EVENT;
    }

    @NotNull
    @Override
    public String getID() {
        return "while_along_path";
    }

    @NotNull
    @Override
    public String getReadableName() {
        return "While Along Path";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Triggered throughout the journey of a spell, like a projectile shape whilst airborne or across an entire beam shape";
    }

    @NotNull
    @Override
    public Set<Module> getCompatibleModifierModules() {
        return super.getCompatibleModifierModules();
    }

    @Override
    public boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @NotNull SpellStack spellStack) {
        return super.run(world, caster, spellStack);
    }

    @NotNull
    @Override
    public Module copy() {
        ModuleEventAlongPath clone = new ModuleEventAlongPath();
        clone.extraModifiers = extraModifiers;
        clone.children = children;
        return clone;
    }
}
