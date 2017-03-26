package com.teamwizardry.wizardry.common.module.events;

import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.RegisterModule;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEventAlongPath extends Module {

	public ModuleEventAlongPath() {
		process(this);
	}

	@Nonnull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Items.FEATHER);
	}

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EVENT;
	}

	@Nonnull
	@Override
	public String getID() {
		return "while_along_path";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "While Along Path";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Triggered throughout the journey of a spell, like a projectile shape whilst airborne or across an entire beam shape";
	}

	@Nonnull
	@Override
	public ModuleEventAlongPath copy() {
		ModuleEventAlongPath module = new ModuleEventAlongPath();
		module.deserializeNBT(serializeNBT());
		process(module);
		return module;
	}
}
