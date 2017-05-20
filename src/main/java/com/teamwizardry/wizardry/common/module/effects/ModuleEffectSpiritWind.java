package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.RegisterModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectSpiritWind extends Module {

	@Nonnull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Items.CLAY_BALL);
	}

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@Nonnull
	@Override
	public String getID() {
		return "effect_spirit_wind";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Spirit Wind";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Spirits will move you to the target location ignoring gravity.";
	}

	@Override
	public double getManaDrain() {
		return 1000;
	}

	@Override
	public double getBurnoutFill() {
		return 1000;
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		return true;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {

	}

	@Nullable
	@Override
	public Color getPrimaryColor() {
		return Color.MAGENTA;
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleEffectSpiritWind());
	}
}
