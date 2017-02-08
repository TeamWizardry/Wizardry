package com.teamwizardry.wizardry.common.module.modifiers;

import com.teamwizardry.wizardry.api.spell.*;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Created by LordSaad.
 */
@RegisterModule
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
		return 50;
	}

	@Override
	public double getBurnoutToFill() {
		return 50;
	}

	@Override
	public void apply(Module module) {
		int power = 2;
		module.attributes.setDouble(Attributes.EXTEND, module.attributes.getDouble(Attributes.EXTEND) + power);
	}

	@NotNull
	@Override
	public ModuleModifierExtend copy() {
		ModuleModifierExtend module = new ModuleModifierExtend();
		module.deserializeNBT(serializeNBT());
		process(module);
		return module;
	}
}
