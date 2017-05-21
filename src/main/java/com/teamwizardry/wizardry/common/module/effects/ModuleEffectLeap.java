package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectLeap extends Module implements IParticleDanger {

	@Nonnull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Items.RABBIT_FOOT);
	}

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
	public double getManaDrain() {
		return 50;
	}

	@Override
	public double getBurnoutFill() {
		return 20;
	}

	@Nullable
	@Override
	public Color getPrimaryColor() {
		return Color.YELLOW;
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		float yaw = spell.getData(YAW, 0F);
		float pitch = spell.getData(PITCH, 0F);
		Vec3d pos = spell.getData(TARGET_HIT);
		Entity target = spell.getData(ENTITY_HIT);

		if (target == null) return false;
		Vec3d lookVec = PosUtils.vecFromRotations(pitch, yaw);

		if (!target.hasNoGravity()) {
			double strength = 0.75;
			if (attributes.hasKey(Attributes.EXTEND))
				strength += Math.min(128.0 / 100.0, attributes.getDouble(Attributes.EXTEND) / 100.0);
			strength *= calcBurnoutPercent(target);

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

		if (position == null) return;

		if (caster != null) {
			if (!caster.hasNoGravity())
				LibParticles.AIR_THROTTLE(spell.world, position, caster, getPrimaryColor(), Color.WHITE, 0.5, true);
		} else LibParticles.AIR_THROTTLE(spell.world, position, position, getPrimaryColor(), Color.WHITE, 0.5);

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
