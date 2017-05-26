package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpCircle;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.api.util.InterpScale;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;


/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleShapeZone extends Module implements IlingeringModule {

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
		Vec3d targetPos = spell.hasData(BLOCK_HIT) ? new Vec3d(spell.getData(BLOCK_HIT, BlockPos.ORIGIN)) : spell.getData(TARGET_HIT);

		if (targetPos == null) return false;

		double radius = 5;
		if (attributes.hasKey(Attributes.EXTEND))
			radius += Math.min(32, attributes.getDouble(Attributes.EXTEND));

		List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(caster, new AxisAlignedBB(new BlockPos(targetPos)).expand(radius, 0, radius).expand(0, 1, 0));

		for (Entity entity : entities) {
			if (entity.getDistance(targetPos.xCoord, targetPos.yCoord, targetPos.zCoord) <= radius) {
				if (ThreadLocalRandom.current().nextInt((int) Math.abs(50000 - (radius * 1000))) != 0) continue;
				if (!processCost(radius, spell)) return false;

				SpellData copy = spell.copy();
				copy.processEntity(entity, false);
				copy.addData(YAW, 0f);
				copy.addData(PITCH, -90f);
				copy.addData(ORIGIN, entity.getPositionVector());
				runNextModule(copy);
			}
		}

		for (int i = (int) -radius; i < radius; i++)
			for (int j = (int) -radius; j < radius; j++)
				for (int k = (int) -radius; k < radius; k++) {
					BlockPos newPos = new BlockPos(targetPos).add(i, j, k);
					if (ThreadLocalRandom.current().nextInt((int) Math.abs(50000 - (radius * 1000))) != 0) continue;
					if (newPos.getDistance((int) targetPos.xCoord, (int) targetPos.yCoord, (int) targetPos.zCoord) <= radius) {
						SpellData copy = spell.copy();
						copy.processBlock(newPos, EnumFacing.VALUES[ThreadLocalRandom.current().nextInt(EnumFacing.VALUES.length - 1)], new Vec3d(newPos).addVector(0.5, 0.5, 0.5));
						copy.addData(YAW, 0f);
						copy.addData(PITCH, -90f);
						runNextModule(copy);
					}
				}

		return true;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {
		Vec3d target = spell.getData(TARGET_HIT);

		if (target == null) return;
		if (ThreadLocalRandom.current().nextInt(10) != 0) return;

		double radius = 5;
		if (attributes.hasKey(Attributes.EXTEND))
			radius += Math.min(32, attributes.getDouble(Attributes.EXTEND));

		ParticleBuilder glitter = new ParticleBuilder(10);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.setScaleFunction(new InterpScale(1, 0));
		glitter.setCollision(true);
		ParticleSpawner.spawn(glitter, spell.world, new InterpCircle(target, new Vec3d(0, 1, 0), (float) radius, 1, ThreadLocalRandom.current().nextFloat()), (int) (radius * 2), 0, (aFloat, particleBuilder) -> {
			glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));
			glitter.setLifetime(ThreadLocalRandom.current().nextInt(10, 20));
			glitter.setColorFunction(new InterpColorHSV(getPrimaryColor(), getSecondaryColor()));
			glitter.addMotion(new Vec3d(
					ThreadLocalRandom.current().nextDouble(-0.001, 0.001),
					ThreadLocalRandom.current().nextDouble(-0.1, 0.1),
					ThreadLocalRandom.current().nextDouble(-0.001, 0.001)
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
		Entity caster = spell.getData(CASTER);
		int strength = 60;
		if (attributes.hasKey(Attributes.EXTEND))
			strength += Math.min(300, attributes.getDouble(Attributes.EXTEND) * 4.6875);
		strength *= 30;
		strength *= calcBurnoutPercent(caster);
		return strength;
	}
}
