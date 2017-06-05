package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.awt.*;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectBurn extends Module implements ITaxing {

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

		double strength = 1 * getMultiplier();
		if (attributes.hasKey(Attributes.EXTEND))
			strength += Math.min(30, attributes.getDouble(Attributes.EXTEND));
		strength *= calcBurnoutPercent(caster);

		if (!tax(this, spell)) return false;

		if (targetEntity != null) targetEntity.setFire((int) strength * 3);

		if (targetPos != null) {
			strength /= 2.0;
			for (int x = (int) strength; x >= -strength; x--)
				for (int y = (int) strength; y >= -strength; y--)
					for (int z = (int) strength; z >= -strength; z--) {
						BlockPos pos = targetPos.add(x, y, z);
						for (EnumFacing facing : EnumFacing.VALUES) {
							if (world.isAirBlock(pos.offset(facing)) || world.getBlockState(pos.offset(facing)).getBlock() == Blocks.FIRE) {
								world.setBlockState(pos.offset(facing), Blocks.FIRE.getDefaultState(), 3);
							}
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
		if (RandUtil.nextBoolean()) color = getSecondaryColor();

		LibParticles.EFFECT_BURN(world, position, color);
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleEffectBurn());
	}
}
