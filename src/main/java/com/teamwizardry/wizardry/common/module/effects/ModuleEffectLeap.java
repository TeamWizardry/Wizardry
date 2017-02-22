package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectLeap extends Module implements IParticleDanger {

	public ModuleEffectLeap() {
	}

	@NotNull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Items.RABBIT_FOOT);
	}

	@NotNull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@NotNull
	@Override
	public String getID() {
		return "effect_leap";
	}

	@NotNull
	@Override
	public String getReadableName() {
		return "Leap";
	}

	@NotNull
	@Override
	public String getDescription() {
		return "Will throttle you upwards and forwards";
	}

	@Override
	public double getManaToConsume() {
		return 150;
	}

	@Override
	public double getBurnoutToFill() {
		return 100;
	}

	@Nullable
	@Override
	public Color getColor() {
		return Color.YELLOW;
	}

	@Override
	public boolean run(@NotNull SpellData spell) {
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
			return true;
		}
		return false;
	}

	@Override
	public void runClient(@Nullable ItemStack stack, @NotNull SpellData spell) {
		Entity caster = spell.getData(CASTER);
		Vec3d position = spell.getData(TARGET_HIT);

		if (position == null) return;

		if (caster != null) {
			if (!caster.hasNoGravity())
				LibParticles.AIR_THROTTLE(spell.world, position, caster, getColor(), Color.WHITE, 0.5, true);
		} else LibParticles.AIR_THROTTLE(spell.world, position, position, getColor(), Color.WHITE, 0.5, true);

	}

	@Override
	public void runClient(@NotNull World world, @Nullable ItemStack stack, @Nullable EntityLivingBase caster, @NotNull Vec3d pos) {
		if (caster != null && !caster.hasNoGravity())
			LibParticles.AIR_THROTTLE(world, pos, caster, getColor(), Color.WHITE, 0.5, true);
	}

	@NotNull
	@Override
	public ModuleEffectLeap copy() {
		ModuleEffectLeap module = new ModuleEffectLeap();
		module.deserializeNBT(serializeNBT());
		process(module);
		return module;
	}

	@Override
	public int chanceOfParticles() {
		return 3;
	}
}
