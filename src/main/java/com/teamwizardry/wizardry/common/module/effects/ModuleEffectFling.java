package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.RegisterModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.CASTER;
import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.TARGET_HIT;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectFling extends Module {

	public ModuleEffectFling() {
	}

	@Nullable
	@Override
	public Color getColor() {
		return Color.MAGENTA;
	}

	@NotNull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Items.CHORUS_FRUIT_POPPED);
	}

	@NotNull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@NotNull
	@Override
	public String getID() {
		return "effect_fling";
	}

	@NotNull
	@Override
	public String getReadableName() {
		return "Fling";
	}

	@NotNull
	@Override
	public String getDescription() {
		return "Will send the caster flying to the target location";
	}

	@Override
	public double getManaToConsume() {
		return 1000;
	}

	@Override
	public double getBurnoutToFill() {
		return 500;
	}

	@Override
	public boolean run(@NotNull SpellData spell) {
		Entity targetEntity = spell.getData(CASTER);
		Vec3d to = spell.getData(TARGET_HIT);

		if (targetEntity == null || to == null) return false;

		Vec3d from = targetEntity.getPositionVector();

		double gravity = 1;
		int heightGain = 5;

		double endGain = to.yCoord - from.yCoord;
		double horizDist = Math.sqrt(to.squareDistanceTo(from));

		double maxGain = heightGain > (endGain + heightGain) ? heightGain : (endGain + heightGain);

		double a = -horizDist * horizDist / (4 * maxGain);
		double c = -endGain;

		double slope = -horizDist / (2 * a) - Math.sqrt(horizDist * horizDist - 4 * a * c) / (2 * a);

		double vy = Math.sqrt(maxGain * gravity);

		double vh = vy / slope;

		double dx = to.xCoord - from.xCoord;
		double dz = to.zCoord - from.zCoord;
		double mag = Math.sqrt(dx * dx + dz * dz);
		double dirx = dx / mag;
		double dirz = dz / mag;

		double vx = vh * dirx;
		double vz = vh * dirz;

		targetEntity.motionX = vx;
		targetEntity.motionY = vy;
		targetEntity.motionZ = vz;
		targetEntity.velocityChanged = true;

		return true;
	}

	@Override
	public void runClient(@NotNull World world, @Nullable ItemStack stack, @Nullable EntityLivingBase caster, @NotNull Vec3d pos) {
	}

	@NotNull
	@Override
	public ModuleEffectFling copy() {
		ModuleEffectFling module = new ModuleEffectFling();
		module.deserializeNBT(serializeNBT());
		process(module);
		return module;
	}
}
