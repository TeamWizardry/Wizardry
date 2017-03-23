package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.RegisterModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.CASTER;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleShapeSelf extends Module {

	@NotNull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Items.DIAMOND);
	}

	@NotNull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.SHAPE;
	}

	@NotNull
	@Override
	public String getID() {
		return "shape_self";
	}

	@NotNull
	@Override
	public String getReadableName() {
		return "Self";
	}

	@NotNull
	@Override
	public String getDescription() {
		return "Will run the spell on the caster";
	}

	@Override
	public boolean run(@NotNull SpellData spell) {
		Entity caster = spell.getData(CASTER);
		if (caster == null) return false;
		spell.crunchData(caster, true);
		spell.crunchData(caster, false);

		usedShape = this;
		return runNextModule(spell);
	}

	@NotNull
	@Override
	public ModuleShapeSelf copy() {
		ModuleShapeSelf module = new ModuleShapeSelf();
		module.deserializeNBT(serializeNBT());
		process(module);
		return module;
	}
}
