package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.ITargettable;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.RegisterModule;
import com.teamwizardry.wizardry.common.entity.EntitySpellGravityWell;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectGravityWell extends Module implements ITargettable {

	public ModuleEffectGravityWell() {
	}

	@Nullable
	@Override
	public Color getColor() {
		return Color.MAGENTA;
	}

	@NotNull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Items.SLIME_BALL);
	}

	@NotNull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@NotNull
	@Override
	public String getID() {
		return "effect_gravity_well";
	}

	@NotNull
	@Override
	public String getReadableName() {
		return "Gravity Well";
	}

	@NotNull
	@Override
	public String getDescription() {
		return "Will suck in all entities around the target.";
	}

	@Override
	public double getManaToConsume() {
		return 1000;
	}

	@Override
	public double getBurnoutToFill() {
		return 1000;
	}

	@Override
	public boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @Nullable Vec3d target) {
		EntitySpellGravityWell well = new EntitySpellGravityWell(world, caster, target, 500, 50);
		if (target != null) well.setPosition(target.xCoord, target.yCoord, target.zCoord);
		world.spawnEntity(well);
		setTargetPosition(this, target);
		return world.spawnEntity(well);
	}

	@Override
	public boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @Nullable Entity target) {
		if (target == null) return false;
		EntitySpellGravityWell well = new EntitySpellGravityWell(world, caster, target.getPositionVector(), 500, 50);
		well.setPosition(target.posX, target.posY, target.posZ);
		world.spawnEntity(well);
		setTargetPosition(this, target.getPositionVector());
		return world.spawnEntity(well);
	}

	@Override
	public void runClient(@NotNull World world, @NotNull ItemStack stack, @Nullable EntityLivingBase caster, @NotNull Vec3d pos) {
	}

	@NotNull
	@Override
	public ModuleEffectGravityWell copy() {
		ModuleEffectGravityWell module = new ModuleEffectGravityWell();
		module.deserializeNBT(serializeNBT());
		process(module);
		return module;
	}
}
