package com.teamwizardry.wizardry.common.module.events;

import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.RegisterModule;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEventAlongPath extends Module {

    public ModuleEventAlongPath() {
        process(this);
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
	public ModuleEventAlongPath copy() {
		ModuleEventAlongPath module = new ModuleEventAlongPath();
		module.deserializeNBT(serializeNBT());
        process(module);
        return module;
    }
}
