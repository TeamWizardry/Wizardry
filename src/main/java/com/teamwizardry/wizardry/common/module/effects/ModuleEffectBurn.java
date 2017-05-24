package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectBurn extends Module implements IContinousSpell {

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@Nonnull
	@Override
	public String getID() {
		return "effect_burn";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Burn";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will burn the target block or entity.";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		World world = spell.world;
		Entity targetEntity = spell.getData(ENTITY_HIT);
		BlockPos targetPos = spell.getData(BLOCK_HIT);
		Entity caster = spell.getData(CASTER);

		int strength = 3;
		if (attributes.hasKey(Attributes.EXTEND))
			strength += Math.min(30, attributes.getDouble(Attributes.EXTEND));

		if (!processCost(strength / 10.0, spell)) return false;

		strength *= calcBurnoutPercent(caster);

		if (targetEntity != null) targetEntity.setFire(strength);

		if (targetPos != null) {
			// TODO: increase radius with strength
			for (EnumFacing facing : EnumFacing.VALUES) {
				if (world.isAirBlock(targetPos.offset(facing)) || world.getBlockState(targetPos.offset(facing)).getBlock() == Blocks.FIRE) {
					world.setBlockState(targetPos.offset(facing), Blocks.FIRE.getDefaultState(), 11);
					return true;
				}
			}
		}
		return true;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {
		World world = spell.world;
		Vec3d position = spell.getData(TARGET_HIT);

		if (position == null) return;

		Color color = getPrimaryColor();
		if (ThreadLocalRandom.current().nextBoolean()) color = getSecondaryColor();

		List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(new BlockPos(position)));
		if (!entities.isEmpty())
			LibParticles.EFFECT_BURN(world, entities.get(0).getPositionVector().addVector(0, entities.get(0).height / 2, 0), color);
		else LibParticles.EFFECT_BURN(world, position.addVector(0, 0.5, 0), color);
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleEffectBurn());
	}
}
