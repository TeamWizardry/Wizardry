package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.Attributes;
import com.teamwizardry.wizardry.api.spell.ITargettable;
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
public class ModuleEffectLeap extends Module implements ITargettable {

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
		return 100;
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
	public boolean run(@NotNull World world, @Nullable EntityLivingBase caster) {
		return super.run(world, caster);
	}

	@Override
	public boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @Nullable Vec3d target) {
		return false;
	}

	@Override
	public boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @Nullable Entity target) {
		if (target != null && !target.hasNoGravity()) {
			double strength = 0.5;
			if (attributes.hasKey(Attributes.EXTEND))
				strength += Math.min(64.0 / 100.0, attributes.getDouble(Attributes.EXTEND) / 100.0);
			if (caster != null && getCap(caster) != null)
				strength *= calcBurnoutPercent(getCap(caster));
			target.motionX = target.isCollidedVertically ? target.getLookVec().xCoord : target.getLookVec().xCoord / 2;
			target.motionY = target.isCollidedVertically ? strength : strength / 3;
			target.motionZ = target.isCollidedVertically ? target.getLookVec().zCoord : target.getLookVec().zCoord / 2;
			target.fallDistance = 0;
			return true;
		}
		return false;
	}

	@Override
	public void runClient(@NotNull World world, @NotNull ItemStack stack, @Nullable EntityLivingBase caster, @NotNull Vec3d pos) {
		double strength = 1;
		if (attributes.hasKey(Attributes.EXTEND)) strength += attributes.getDouble(Attributes.EXTEND);
		if (caster != null && !caster.hasNoGravity()) {
			LibParticles.EFFECT_LEAP(world, getColor(), pos, strength);
			LibParticles.AIR_THROTTLE(world, pos, caster, getColor(), Color.WHITE);
		}
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
