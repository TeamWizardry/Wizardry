package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.wizardry.api.spell.IContinuousModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.attribute.Attributes;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleShape;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RayTrace;
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
public class ModuleShapeBeam extends ModuleShape implements IContinuousModule {

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

		double range = getModifier(spell, Attributes.RANGE, 10, 100);
		double potency = 20 - getModifier(spell, Attributes.POTENCY, 1, 19);

		setCostMultiplier(this, 0.1);

		RayTraceResult trace = new RayTrace(world, PosUtils.vecFromRotations(pitch, yaw), position, range).setSkipEntity(caster).setReturnLastUncollidableBlock(true).setIgnoreBlocksWithoutBoundingBoxes(false).trace();
		if (trace == null) return false;

		if (trace.typeOfHit == RayTraceResult.Type.ENTITY)
			spell.processEntity(trace.entityHit, false);
		else if (trace.typeOfHit == RayTraceResult.Type.BLOCK)
			spell.processBlock(trace.getBlockPos(), trace.sideHit, trace.hitVec);

		return RandUtil.nextInt((int) potency) == 0 && runNextModule(spell);
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
			origin = new Vec3d(offX, 0, offZ).add(position);
		}
		LibParticles.SHAPE_BEAM(world, target, origin, getPrimaryColor());
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleShapeBeam());
	}
}
