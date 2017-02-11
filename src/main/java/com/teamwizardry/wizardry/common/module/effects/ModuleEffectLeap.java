package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.Attributes;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.RegisterModule;
import com.teamwizardry.wizardry.lib.LibParticles;
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
public class ModuleEffectLeap extends Module {

	public ModuleEffectLeap() {
	}

	@NotNull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Items.RABBIT_FOOT);
	}

	@NotNull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@NotNull
	@Override
	public String getID() {
		return "effect_leap";
	}

	@NotNull
	@Override
	public String getReadableName() {
		return "Leap";
	}

	@NotNull
	@Override
	public String getDescription() {
		return "Will throttle you upwards and forwards";
	}

	@Override
	public double getManaToConsume() {
		return 150;
	}

	@Override
	public double getBurnoutToFill() {
		return 100;
	}

	@Nullable
	@Override
	public Color getColor() {
		return Color.YELLOW;
	}

	@Override
	public boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @NotNull Entity target) {
		if (!target.hasNoGravity()) {
			double strength = 0.75;
			if (attributes.hasKey(Attributes.EXTEND))
				strength += Math.min(128.0 / 100.0, attributes.getDouble(Attributes.EXTEND) / 100.0);
			strength *= calcBurnoutPercent(target);

			if (getTargetPosition() == null)
				target.motionX = target.isCollidedVertically ? target.getLookVec().xCoord : target.getLookVec().xCoord / 2.0;
			else
				target.motionX = target.isCollidedVertically ? getTargetPosition().xCoord : getTargetPosition().xCoord / 2.0;

			target.motionY = target.isCollidedVertically ? strength : 0.4 * calcBurnoutPercent(target);

			if (getTargetPosition() == null)
				target.motionZ = target.isCollidedVertically ? target.getLookVec().zCoord : target.getLookVec().zCoord / 2.0;
			else
				target.motionZ = target.isCollidedVertically ? getTargetPosition().zCoord : getTargetPosition().zCoord / 2.0;

			target.velocityChanged = true;
			target.fallDistance /= 2 * calcBurnoutPercent(target);
			return true;
		}
		return false;
	}

	@Override
	public void runClient(@NotNull World world, @Nullable ItemStack stack, @Nullable EntityLivingBase caster, @NotNull Vec3d pos) {
		if (caster != null && !caster.hasNoGravity())
			LibParticles.AIR_THROTTLE(world, pos, caster, getColor(), Color.WHITE, 0.5, true);
	}

	@NotNull
	@Override
	public ModuleEffectLeap copy() {
		ModuleEffectLeap module = new ModuleEffectLeap();
		module.deserializeNBT(serializeNBT());
		process(module);
		return module;
	}
}
