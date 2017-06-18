package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpLine;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RayTrace;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;


/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleShapeCone extends Module implements ICostModifier {

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.SHAPE;
	}

	@Nonnull
	@Override
	public String getID() {
		return "shape_cone";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Cone";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will run the spell in a circular arc in front of the caster";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		World world = spell.world;
		float yaw = spell.getData(YAW, 0F);
		float pitch = spell.getData(PITCH, 0F);
		Vec3d position = spell.getData(ORIGIN);
		Entity caster = spell.getData(CASTER);

		if (position == null) return false;

		double range = getModifierPower(spell, Attributes.EXTEND_RANGE, 10, 32, true, true);

		setCostMultiplier(this, range / 16.0);

		Vec3d origin = position;
		if (caster != null) {
			float offX = 0.5f * (float) Math.sin(Math.toRadians(-90.0f - yaw));
			float offZ = 0.5f * (float) Math.cos(Math.toRadians(-90.0f - yaw));
			origin = new Vec3d(offX, 0, offZ).add(position);
		}

		int chance = (int) (getModifierPower(spell, Attributes.INCREASE_POTENCY, 5, 32, true, true));

		for (int i = 0; i < chance; i++) {
			//	if (chance > 0 && RandUtil.nextInt(33 - chance) != 0) continue;

			double angle = range;
			float newPitch = (float) (pitch + RandUtil.nextDouble(-angle * 6, angle * 6));
			float newYaw = (float) (yaw + RandUtil.nextDouble(-angle * 6, angle * 6));

			Vec3d target = PosUtils.vecFromRotations(newPitch, newYaw);

			SpellData newSpell = spell.copy();

			RayTraceResult result = new RayTrace(world, target.normalize(), origin, range / 2).setSkipEntity(caster).trace();
			newSpell.processBlock(result.getBlockPos(), result.sideHit, result.hitVec);
			if (result.entityHit != null) spell.processEntity(result.entityHit, false);

			castParticles(newSpell);
			runNextModule(newSpell);
		}

		return true;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {
		float yaw = spell.getData(YAW, 0F);
		Entity caster = spell.getData(CASTER);
		Vec3d position = spell.getData(ORIGIN);
		Vec3d target = spell.getData(TARGET_HIT);

		if (position == null || target == null) return;

		float offX = 0.5f * (float) Math.sin(Math.toRadians(-90.0f - yaw));
		float offZ = 0.5f * (float) Math.cos(Math.toRadians(-90.0f - yaw));
		Vec3d origin = new Vec3d(offX, 0, offZ).add(position);

		ParticleBuilder lines = new ParticleBuilder(10);
		lines.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		lines.setScaleFunction(new InterpScale(0.5f, 0));
		lines.setColorFunction(new InterpColorHSV(getPrimaryColor(), getSecondaryColor()));
		ParticleSpawner.spawn(lines, spell.world, new InterpLine(origin, target), (int) target.distanceTo(origin) * 4, 0, (aFloat, particleBuilder) -> {
			lines.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));
			lines.setLifetime(RandUtil.nextInt(10, 20));
		});
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleShapeCone());
	}

}
