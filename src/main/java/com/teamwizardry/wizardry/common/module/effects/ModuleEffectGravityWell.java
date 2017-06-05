package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpHelix;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.api.util.InterpScale;
import com.teamwizardry.wizardry.api.util.RandUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectGravityWell extends Module implements IlingeringModule, ITaxing {

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@Nonnull
	@Override
	public String getID() {
		return "effect_gravity_well";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Gravity Well";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will suck in all entities around the target.";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		World world = spell.world;
		Vec3d position = spell.getData(TARGET_HIT);
		Entity caster = spell.getData(CASTER);

		if (position == null) return false;

		double strength = 20 * getMultiplier();
		if (attributes.hasKey(Attributes.EXTEND))
			strength += attributes.getDouble(Attributes.EXTEND);
		strength *= calcBurnoutPercent(caster);

		for (Entity entity : world.getEntitiesWithinAABBExcludingEntity(caster, new AxisAlignedBB(new BlockPos(position)).expand(strength, strength, strength))) {
			if (entity == null) continue;
			double dist = entity.getPositionVector().distanceTo(position);
			if (dist < 2) continue;
			if (dist > strength) continue;
			if (!tax(this, spell)) return false;

			final double upperMag = (strength / 20);
			final double scale = 3.5;
			double mag = upperMag * (scale * dist / (-scale * dist - 1) + 1);

			Vec3d dir = position.subtract(entity.getPositionVector()).normalize().scale(mag);

			entity.motionX += (dir.xCoord);
			entity.motionY += (dir.yCoord);
			entity.motionZ += (dir.zCoord);
			entity.fallDistance = 0;
			entity.velocityChanged = true;

			spell.addData(ENTITY_HIT, entity);
			if (entity instanceof EntityPlayerMP)
				((EntityPlayerMP) entity).connection.sendPacket(new SPacketEntityVelocity(entity));

			runNextModule(spell);
		}

		return true;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {
		Vec3d position = spell.getData(TARGET_HIT);

		if (position == null) return;
		if (RandUtil.nextInt(2) != 0) return;

		ParticleBuilder glitter = new ParticleBuilder(RandUtil.nextInt(20, 30));
		glitter.setColorFunction(new InterpColorHSV(getPrimaryColor(), getSecondaryColor()));
		ParticleSpawner.spawn(glitter, spell.world, new StaticInterp<>(position), 5, RandUtil.nextInt(0, 30), (aFloat, particleBuilder) -> {
			glitter.setScale((float) RandUtil.nextDouble(0.3, 1));
			glitter.setAlphaFunction(new InterpFadeInOut(0.3f, (float) RandUtil.nextDouble(0.6, 1)));
			glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			glitter.setLifetime(RandUtil.nextInt(10, 40));
			glitter.setScaleFunction(new InterpScale(1, 0));
			if (RandUtil.nextBoolean())
				glitter.setPositionFunction(new InterpHelix(
						new Vec3d(0, 0, 0),
						new Vec3d(0, 2, 0),
						0.5f, 0, 1, RandUtil.nextFloat()
				));
			else glitter.setPositionFunction(new InterpHelix(
					new Vec3d(0, 0, 0),
					new Vec3d(0, -2, 0),
					0.5f, 0, 1, RandUtil.nextFloat()
			));
		});
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleEffectGravityWell());
	}

	@Override
	public int lingeringTime(SpellData spell) {
		Entity caster = spell.getData(CASTER);
		int strength = 1000;
		if (attributes.hasKey(Attributes.EXTEND))
			strength += attributes.getDouble(Attributes.EXTEND) * 10;
		strength *= calcBurnoutPercent(caster);

		return strength;
	}
}
