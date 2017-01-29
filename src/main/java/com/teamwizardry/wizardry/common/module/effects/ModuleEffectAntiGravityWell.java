package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.Attributes;
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
public class ModuleEffectAntiGravityWell extends Module implements ITargettable {

	public ModuleEffectAntiGravityWell() {
	}

	@Nullable
	@Override
	public Color getColor() {
		return Color.MAGENTA;
	}

	@NotNull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Items.MAGMA_CREAM);
	}

	@NotNull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@NotNull
	@Override
	public String getID() {
		return "effect_anti_gravity_well";
	}

	@NotNull
	@Override
	public String getReadableName() {
		return "Anti Gravity Well";
	}

	@NotNull
	@Override
	public String getDescription() {
		return "Will disperse in all entities around the target.";
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
	public boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @NotNull Vec3d target) {
		double strength = 20;
		if (attributes.hasKey(Attributes.EXTEND))
			strength += attributes.getDouble(Attributes.EXTEND);
		EntitySpellGravityWell well = new EntitySpellGravityWell(world, caster, target, (int) (strength * 20), strength, true);
		well.setPosition(target.xCoord, target.yCoord, target.zCoord);
		world.spawnEntity(well);
		setTargetPosition(this, target);
		return world.spawnEntity(well);
	}

	@Override
	public boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @NotNull Entity target) {
		double strength = 20;
		if (attributes.hasKey(Attributes.EXTEND))
			strength += attributes.getDouble(Attributes.EXTEND);
		if (target instanceof EntityLivingBase)
			strength *= calcBurnoutPercent(getCap((EntityLivingBase) target));
		EntitySpellGravityWell well = new EntitySpellGravityWell(world, caster, target.getPositionVector(), (int) (strength * 20), strength, true);
		well.setPosition(target.posX, target.posY, target.posZ);
		world.spawnEntity(well);
		setTargetPosition(this, target.getPositionVector());
		return world.spawnEntity(well);
	}

	@Override
	public void runClient(@NotNull World world, @Nullable ItemStack stack, @Nullable EntityLivingBase caster, @NotNull Vec3d pos) {
	}

	@NotNull
	@Override
	public ModuleEffectAntiGravityWell copy() {
		ModuleEffectAntiGravityWell module = new ModuleEffectAntiGravityWell();
		module.deserializeNBT(serializeNBT());
		process(module);
		return module;
	}
}
