package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.CASTER;
import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.TARGET_HIT;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectFling extends ModuleEffect {

	@Nonnull
	@Override
	public String getID() {
		return "effect_fling";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Fling";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will send the caster flying to the target location";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		Entity targetEntity = spell.getData(CASTER);
		Vec3d to = spell.getData(TARGET_HIT);

		if (targetEntity == null || to == null) return false;
		if (!tax(this, spell)) return false;

		Vec3d from = targetEntity.getPositionVector();

		double gravity = 1;
		int heightGain = 5;

		double endGain = to.y - from.y;
		double horizDist = Math.sqrt(to.squareDistanceTo(from));

		double maxGain = heightGain > (endGain + heightGain) ? heightGain : (endGain + heightGain);

		double a = -horizDist * horizDist / (4 * maxGain);
		double c = -endGain;

		double slope = -horizDist / (2 * a) - Math.sqrt(horizDist * horizDist - 4 * a * c) / (2 * a);

		double vy = Math.sqrt(maxGain * gravity);

		double vh = vy / slope;

		double dx = to.x - from.x;
		double dz = to.z - from.z;
		double mag = Math.sqrt(dx * dx + dz * dz);
		double dirx = dx / mag;
		double dirz = dz / mag;

		double vx = vh * dirx;
		double vz = vh * dirz;

		targetEntity.motionX = vx;
		targetEntity.motionY = vy;
		targetEntity.motionZ = vz;
		targetEntity.velocityChanged = true;

		return true;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {

	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleEffectFling());
	}
}
