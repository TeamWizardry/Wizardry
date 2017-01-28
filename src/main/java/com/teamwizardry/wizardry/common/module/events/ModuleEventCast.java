package com.teamwizardry.wizardry.common.module.events;

import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by LordSaad.
 */
public class ModuleEventCast extends Module {

    public ModuleEventCast() {
        process(this);
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


    @Override
    public boolean run(@NotNull World world, @Nullable EntityLivingBase caster) {
        return false;
    }

    @NotNull
    @Override
    public ModuleEventCast copy() {
        ModuleEventCast module = new ModuleEventCast();
        module.deserializeNBT(serializeNBT());
        process(module);
        return module;
    }
}
