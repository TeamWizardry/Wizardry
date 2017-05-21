package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.RegisterModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.CASTER;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleShapeTouch extends Module {

	@Nonnull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Items.BEEF);
	}

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.SHAPE;
	}

	@Override
	public double getManaDrain() {
		return 100;
	}

	@Override
	public double getBurnoutFill() {
		return 20;
	}

	@Nonnull
	@Override
	public String getID() {
		return "shape_touch";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Touch";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will run the spell on the caster";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		Entity caster = spell.getData(CASTER);
		if (caster == null) return false;
		spell.processEntity(caster, true);
		spell.processEntity(caster, false);

		return runNextModule(spell);
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {

	}

	@Override
	public int getCooldownTime() {
		return 10;
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleShapeTouch());
	}
}
