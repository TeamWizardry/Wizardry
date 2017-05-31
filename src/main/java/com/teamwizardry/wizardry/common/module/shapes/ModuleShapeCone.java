package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpLine;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.api.util.ColorUtils;
import com.teamwizardry.wizardry.api.util.InterpScale;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;


/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleShapeCone extends Module {

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

		double range = 5;
		if (attributes.hasKey(Attributes.EXTEND)) range += attributes.getDouble(Attributes.EXTEND);
		Vec3d origin = position;
		if (caster != null) {
			float offX = 0.5f * (float) Math.sin(Math.toRadians(-90.0f - yaw));
			float offZ = 0.5f * (float) Math.cos(Math.toRadians(-90.0f - yaw));
			origin = new Vec3d(offX, caster.getEyeHeight(), offZ).add(position);
		}

		setStrengthMultiplier((float) (1 / range));

		int chance = 0;
		if (attributes.hasKey(Attributes.EXTEND))
			chance = (int) Math.min(3, chance + attributes.getDouble(Attributes.EXTEND));

		for (int i = 0; i < range; i++) {
			if (!processCost(range / 10.0, spell)) return false;

			if (chance > 0 && ThreadLocalRandom.current().nextInt(chance) != 0) continue;

			double angle = Math.min(8, range);
			float newPitch = (float) (pitch + ThreadLocalRandom.current().nextDouble(-angle * 6, angle * 6));
			float newYaw = (float) (yaw + ThreadLocalRandom.current().nextDouble(-angle * 6, angle * 6));

			Vec3d target = PosUtils.vecFromRotations(newPitch, newYaw);

			SpellData newSpell = spell.copy();

			RayTraceResult result = Utils.raytrace(world, target.normalize(), origin, range / 2, caster);
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
		Vec3d origin = new Vec3d(offX, caster == null ? 0 : caster.getEyeHeight(), offZ).add(position);

		ParticleBuilder lines = new ParticleBuilder(10);
		lines.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		lines.setScaleFunction(new InterpScale(0.5f, 0));
		ParticleSpawner.spawn(lines, spell.world, new InterpLine(origin, target), (int) target.distanceTo(origin) * 4, 0, (aFloat, particleBuilder) -> {
			lines.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));
			lines.setLifetime(ThreadLocalRandom.current().nextInt(10, 20));
			lines.setColor(ColorUtils.changeColorAlpha(getPrimaryColor() != null ? getPrimaryColor() : Color.WHITE, ThreadLocalRandom.current().nextInt(50, 150)));
		});
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleShapeCone());
	}
}
