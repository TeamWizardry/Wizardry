package com.teamwizardry.wizardry.common.spell.module.events;

import com.teamwizardry.wizardry.api.spell.IModule;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.SpellStack;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Created by LordSaad.
 */
public class ModuleEventCast extends IModule {

    public ModuleEventCast() {
    }

    @NotNull
    @Override
    public ItemStack getRequiredStack() {
        return new ItemStack(ModItems.JAR, 1, 2);
    }

    @NotNull
    @Override
    public ModuleType getModuleType() {
        return ModuleType.EVENT;
    }

    @NotNull
    @Override
    public String getID() {
        return "on_cast";
    }

    @NotNull
    @Override
    public String getReadableName() {
        return "On Spell Cast";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Triggered when this spell is ran";
    }


    @NotNull
    @Override
    public Set<IModule> getCompatibleModifierModules() {
        return super.getCompatibleModifierModules();
    }

    @Override
    public boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @NotNull SpellStack spellStack) {
        return false;
    }

    @NotNull
    @Override
    public IModule copy() {
        ModuleEventCast clone = new ModuleEventCast();
        clone.modifierModules = modifierModules;
        clone.extraModifiers = extraModifiers;
        return clone;
    }
}
