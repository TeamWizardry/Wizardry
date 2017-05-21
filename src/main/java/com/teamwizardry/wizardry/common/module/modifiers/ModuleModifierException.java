package com.teamwizardry.wizardry.common.module.modifiers;

import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleModifierException extends Module implements IModifier {

	@Nonnull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(ModItems.SYRINGE, 1, 3);
	}

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.MODIFIER;
	}

	@Nonnull
	@Override
	public String getID() {
		return "modifier_exception";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Exception";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will ignore the entity chosen";
	}

	@Override
	public double getManaDrain() {
		return 5;
	}

	@Override
	public double getBurnoutFill() {
		return 5;
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		return true;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {

	}

	@Override
	public void apply(Module module) {
		// TODO
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleModifierException());
	}
}
