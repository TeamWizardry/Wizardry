package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectThrive extends Module implements IContinousSpell {

	public ModuleEffectThrive() {
	}

	@NotNull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Blocks.BONE_BLOCK);
	}

	@NotNull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@NotNull
	@Override
	public String getID() {
		return "thrive";
	}

	@NotNull
	@Override
	public String getReadableName() {
		return "Thrive";
	}

	@NotNull
	@Override
	public String getDescription() {
		return "Will heal entities & speed up plant growth";
	}

	@Override
	public double getManaToConsume() {
		return 100;
	}

	@Override
	public double getBurnoutToFill() {
		return 500;
	}

	@Nullable
	@Override
	public Color getColor() {
		return Color.RED;
	}

	@Override
	public boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @NotNull Entity target) {
		if (!(target instanceof EntityLivingBase)) return false;
		double strength = 0.3;
		if (attributes.hasKey(Attributes.EXTEND))
			strength += Math.min(20.0 / 10.0, attributes.getDouble(Attributes.EXTEND) / 10.0);
		strength *= calcBurnoutPercent(caster);

		((EntityLivingBase) target).setHealth((float) (((EntityLivingBase) target).getHealth() + strength));
		return true;
	}

	@Override
	public boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @NotNull Vec3d target) {
		double chance = 80;
		if (attributes.hasKey(Attributes.EXTEND))
			chance -= Math.min(20, attributes.getDouble(Attributes.EXTEND));
		chance *= calcBurnoutPercent(caster);
		if (chance < 0) return false;
		if (ThreadLocalRandom.current().nextInt((int) chance) != 0) return false;

		BlockPos pos = new BlockPos(target);
		if (world.getBlockState(pos).getBlock() instanceof IGrowable)
			ItemDye.applyBonemeal(new ItemStack(Items.DYE), world, pos);

		return true;
	}

	@Override
	public void runClient(@NotNull World world, @Nullable ItemStack stack, @Nullable EntityLivingBase caster, @NotNull Vec3d pos) {
		if (ThreadLocalRandom.current().nextInt(15) != 0) return;

		List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(new BlockPos(pos)));
		if (!entities.isEmpty())
			LibParticles.EFFECT_REGENERATE(world, entities.get(0).getPositionVector().addVector(0, entities.get(0).height / 2, 0), getColor());
		else {
			BlockPos plant = new BlockPos(pos);
			if (world.getBlockState(plant).getBlock() instanceof IGrowable)
				LibParticles.EFFECT_REGENERATE(world, pos, getColor());
		}
	}

	@NotNull
	@Override
	public ModuleEffectThrive copy() {
		ModuleEffectThrive module = new ModuleEffectThrive();
		module.deserializeNBT(serializeNBT());
		process(module);
		return module;
	}
}
