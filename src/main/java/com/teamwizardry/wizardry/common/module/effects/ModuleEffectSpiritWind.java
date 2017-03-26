package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.RegisterModule;
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

	public ModuleEffectSpiritWind() {
	}

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
	public double getManaToConsume() {
		return 1000;
	}

	@Override
	public double getBurnoutToFill() {
		return 1000;
	}

	@Nullable
	@Override
	public Color getColor() {
		return Color.MAGENTA;
	}

	@Nonnull
	@Override
	public ModuleEffectSpiritWind copy() {
		ModuleEffectSpiritWind module = new ModuleEffectSpiritWind();
		module.deserializeNBT(serializeNBT());
		process(module);
		return module;
	}
}
