package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.Utils;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleShapeBeam extends Module implements IContinousSpell, ICostModifier {

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.SHAPE;
	}

	@Nonnull
	@Override
	public String getID() {
		return "shape_beam";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Beam";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will run the spell via a beam emanating from the caster";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		World world = spell.world;
		float yaw = spell.getData(YAW, 0F);
		float pitch = spell.getData(PITCH, 0F);
		Vec3d position = spell.getData(ORIGIN);
		Entity caster = spell.getData(CASTER);
		float strength = spell.getData(STRENGTH, 1f);

		if (position == null) return false;

		double range = 10;
		if (attributes.hasKey(Attributes.EXTEND)) range = Math.min(64, attributes.getDouble(Attributes.EXTEND));

		setCostMultiplier(this, 0.1);

		RayTraceResult trace = Utils.raytrace(world, PosUtils.vecFromRotations(pitch, yaw), caster != null ? position.addVector(0, caster.getEyeHeight(), 0) : position, range, caster);
		if (trace == null) return false;

		if (trace.typeOfHit == RayTraceResult.Type.ENTITY)
			spell.processEntity(trace.entityHit, false);
		else if (trace.typeOfHit == RayTraceResult.Type.BLOCK) {
			spell.processBlock(trace.getBlockPos(), trace.sideHit, trace.hitVec);
		}
		if (trace.hitVec != null) spell.addData(TARGET_HIT, trace.hitVec);

		forceCastNextModuleParticles(spell);
		return runNextModule(spell);
	}


	@Override
	public void runClient(@Nonnull SpellData spell) {
		World world = spell.world;
		float yaw = spell.getData(YAW, 0F);
		Vec3d position = spell.getData(ORIGIN);
		Entity caster = spell.getData(CASTER);
		Vec3d target = spell.getData(TARGET_HIT);

		if (position == null) return;
		if (target == null) return;

		Vec3d origin = position;
		if (caster != null) {
			float offX = 0.5f * (float) Math.sin(Math.toRadians(-90.0f - yaw));
			float offZ = 0.5f * (float) Math.cos(Math.toRadians(-90.0f - yaw));
			origin = new Vec3d(offX, caster.getEyeHeight(), offZ).add(position);
		}
		LibParticles.SHAPE_BEAM(world, target, origin, getPrimaryColor());
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleShapeBeam());
	}
}
