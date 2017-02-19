package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.teamwizardry.wizardry.api.spell.Spell.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectBurn extends Module implements IContinousSpell {

	public ModuleEffectBurn() {
	}

	@NotNull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Items.BLAZE_POWDER);
	}

	@NotNull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@NotNull
	@Override
	public String getID() {
		return "burn";
	}

	@NotNull
	@Override
	public String getReadableName() {
		return "Burn";
	}

	@NotNull
	@Override
	public String getDescription() {
		return "Will burn the target block or entity.";
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
		return new Color(0xFF3C00);
	}

	@Nullable
	@Override
	public Color getSecondaryColor() {
		return new Color(0xA10000);
	}

	@Override
	public boolean run(@NotNull Spell spell) {
		World world = spell.world;
		Entity targetEntity = spell.getData(ENTITY_HIT);
		BlockPos targetPos = spell.getData(BLOCK_HIT);
		Entity caster = spell.getData(CASTER);

		int strength = 1;
		if (attributes.hasKey(Attributes.EXTEND))
			strength += Math.min(80, attributes.getDouble(Attributes.EXTEND));
		strength *= calcBurnoutPercent(caster);

		if (targetEntity != null) targetEntity.setFire(strength);
		if (targetPos != null) {
			double chance = 50;
			if (attributes.hasKey(Attributes.EXTEND))
				chance -= Math.min(20, attributes.getDouble(Attributes.EXTEND));
			chance *= calcBurnoutPercent(caster);
			if ((int) chance <= 0) return false;
			if (ThreadLocalRandom.current().nextInt((int) chance) != 0) return false;

			if (world.isAirBlock(targetPos)) {
				for (EnumFacing facing : EnumFacing.VALUES) {
					if (world.isAirBlock(targetPos.offset(facing)) || world.getBlockState(targetPos.offset(facing)).getBlock() == Blocks.FIRE) {
						world.setBlockState(targetPos, Blocks.FIRE.getDefaultState());
						return true;
					}
				}
			}
		}
		return true;
	}

	@Override
	public void runClient(@NotNull World world, @Nullable ItemStack stack, @Nullable EntityLivingBase caster, @NotNull Vec3d pos) {
		Color color = getColor();
		if (ThreadLocalRandom.current().nextBoolean()) color = getSecondaryColor();

		List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(new BlockPos(pos)));
		if (!entities.isEmpty())
			LibParticles.EFFECT_BURN(world, entities.get(0).getPositionVector().addVector(0, entities.get(0).height / 2, 0), color);
		else LibParticles.EFFECT_BURN(world, pos.addVector(0, 0.5, 0), color);
	}

	@NotNull
	@Override
	public ModuleEffectBurn copy() {
		ModuleEffectBurn module = new ModuleEffectBurn();
		module.deserializeNBT(serializeNBT());
		process(module);
		return module;
	}
}
