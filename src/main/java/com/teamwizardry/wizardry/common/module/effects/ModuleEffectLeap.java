package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectLeap extends Module implements IParticleDanger, ITaxing {

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@Nonnull
	@Override
	public String getID() {
		return "effect_leap";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Leap";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will throttle you upwards and forwards";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		float yaw = spell.getData(YAW, 0F);
		float pitch = spell.getData(PITCH, 0F);
		Vec3d pos = spell.getData(TARGET_HIT);
		Entity target = spell.getData(ENTITY_HIT);
		Entity caster = spell.getData(CASTER);

		if (target == null) return false;

		Vec3d lookVec = PosUtils.vecFromRotations(pitch, yaw);

		if (!target.hasNoGravity()) {
			double strength = 0.75 * getMultiplier();
			if (attributes.hasKey(Attributes.EXTEND))
				strength += Math.min(128.0 / 100.0, attributes.getDouble(Attributes.EXTEND) / 100.0);

			if (!tax(this, spell)) return false;

			strength *= calcBurnoutPercent(caster);

			target.motionX += target.isCollidedVertically ? lookVec.xCoord : lookVec.xCoord / 2.0;

			target.motionY += target.isCollidedVertically ? strength : Math.max(0.5, strength / 3) * calcBurnoutPercent(target);

			target.motionZ += target.isCollidedVertically ? lookVec.zCoord : lookVec.zCoord / 2.0;

			target.velocityChanged = true;
			target.fallDistance /= 2 * calcBurnoutPercent(target);

			if (target instanceof EntityPlayerMP)
				((EntityPlayerMP) target).connection.sendPacket(new SPacketEntityVelocity(target));
			return true;
		}
		return false;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {
		Entity caster = spell.getData(CASTER);
		Vec3d position = spell.getData(TARGET_HIT);
		Entity entityHit = spell.getData(ENTITY_HIT);

		if (position == null) return;
		if (entityHit == null) return;

		if (!entityHit.hasNoGravity()) {
			Vec3d normal = new Vec3d(entityHit.motionX, entityHit.motionY, entityHit.motionZ).normalize().scale(1 / 2.0);

			LibParticles.AIR_THROTTLE(spell.world, position, normal, getPrimaryColor(), getSecondaryColor(), 0.5);

		}
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleEffectLeap());
	}

	@Override
	public int chanceOfParticles() {
		return 3;
	}
}
