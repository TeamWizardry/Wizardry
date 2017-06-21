package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpCircle;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RandUtilSeed;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;


/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleShapeZone extends ModuleShape implements ILingeringModule {

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.SHAPE;
	}

	@Nonnull
	@Override
	public String getID() {
		return "shape_zone";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Zone";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will linger in the area targeted in a circle, continuously running a spell in that region.";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		World world = spell.world;
		Vec3d position = spell.getData(ORIGIN);
		Entity caster = spell.getData(CASTER);
		Vec3d targetPos = spell.getData(TARGET_HIT);
		long seed = RandUtil.nextLong(100, 1000000);
		spell.addData(SEED, seed);

		RandUtilSeed r = new RandUtilSeed(seed);

		if (targetPos == null) return false;

		double aoe = getModifierPower(spell, Attributes.INCREASE_AOE, 3, 10, false, false);
		double strength = getModifierPower(spell, Attributes.INCREASE_POTENCY, 1, 10, true, true);
		double range = getModifierPower(spell, Attributes.EXTEND_RANGE, 1, 10, true, true);

		List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(new BlockPos(targetPos)).expand(aoe, 1, aoe));

		setMultiplier(0.7);
		if (r.nextInt((int) (((100) - strength))) == 0) {
			for (Entity entity : entities) {
				if (entity.getDistance(targetPos.x, targetPos.y, targetPos.z) <= aoe) {
					Vec3d vec = targetPos.addVector(RandUtil.nextDouble(-strength, strength), RandUtil.nextDouble(range), RandUtil.nextDouble(-strength, strength));

					SpellData copy = spell.copy();
					copy.processEntity(entity, false);
					copy.addData(YAW, entity.rotationYaw);
					copy.addData(PITCH, entity.rotationPitch);
					copy.addData(ORIGIN, vec);
					runNextModule(copy);
				}
			}
		}

		if (r.nextInt((int) ((110 - strength * 10))) != 0) return true;
		ArrayList<Vec3d> blocks = new ArrayList<>();
		for (int i = (int) -aoe; i < aoe; i++)
			for (int j = 0; j < 1 + range; j++)
				for (int k = (int) -aoe; k < aoe; k++) {
					Vec3d pos = targetPos.addVector(i, j, k);
					if (pos.distanceTo(targetPos) <= aoe) {
						blocks.add(pos);
					}
				}

		Vec3d pos = blocks.get(RandUtil.nextInt(blocks.size() - 1));

		SpellData copy = spell.copy();
		copy.processBlock(new BlockPos(pos), EnumFacing.UP, pos);
		copy.addData(YAW, RandUtil.nextFloat(-180, 180));
		copy.addData(PITCH, RandUtil.nextFloat(-50, 50));
		runNextModule(copy);
		return true;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {
		Vec3d target = spell.getData(TARGET_HIT);

		if (target == null) return;
		if (RandUtil.nextInt(10) != 0) return;

		double aoe = getModifierPower(spell, Attributes.INCREASE_AOE, 3, 10, false, false);

		ParticleBuilder glitter = new ParticleBuilder(10);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.setScaleFunction(new InterpScale(1, 0));
		glitter.setCollision(true);
		ParticleSpawner.spawn(glitter, spell.world, new InterpCircle(target, new Vec3d(0, 1, 0), (float) aoe, 1, RandUtil.nextFloat()), (int) (aoe * 2), 0, (aFloat, particleBuilder) -> {
			glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));
			glitter.setLifetime(RandUtil.nextInt(10, 20));
			glitter.setColorFunction(new InterpColorHSV(getPrimaryColor(), getSecondaryColor()));
			glitter.addMotion(new Vec3d(
					RandUtil.nextDouble(-0.001, 0.001),
					RandUtil.nextDouble(-0.1, 0.1),
					RandUtil.nextDouble(-0.001, 0.001)
			));
		});
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleShapeZone());
	}

	@Override
	public int lingeringTime(SpellData spell) {
		double strength = getModifierPower(spell, Attributes.EXTEND_TIME, 40, 100, true, true) * 30;
		return (int) strength;
	}
}
