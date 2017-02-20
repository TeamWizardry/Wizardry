package com.teamwizardry.wizardry.common.module.events;

import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.RegisterModule;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEventCast extends Module {

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
        return "On SpellData Cast";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Triggered when this spell is ran";
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
