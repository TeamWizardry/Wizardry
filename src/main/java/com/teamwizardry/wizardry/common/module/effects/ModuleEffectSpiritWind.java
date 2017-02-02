package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.RegisterModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectSpiritWind extends Module {

	public ModuleEffectSpiritWind() {
	}

	@NotNull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Items.CLAY_BALL);
	}

	@NotNull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@NotNull
	@Override
	public String getID() {
		return "effect_spirit_wind";
	}

	@NotNull
	@Override
	public String getReadableName() {
		return "Spirit Wind";
	}

	@NotNull
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

	@Override
	public boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @NotNull Entity target) {
		// TODO
		return false;
	}

	@Override
	public void runClient(@NotNull World world, @Nullable ItemStack stack, @Nullable EntityLivingBase caster, @NotNull Vec3d pos) {

	}

	@NotNull
	@Override
	public ModuleEffectSpiritWind copy() {
		ModuleEffectSpiritWind module = new ModuleEffectSpiritWind();
		module.deserializeNBT(serializeNBT());
		process(module);
		return module;
	}
}
