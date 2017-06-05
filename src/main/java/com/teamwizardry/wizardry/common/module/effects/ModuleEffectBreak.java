package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectBreak extends Module implements ITaxing {

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@Nonnull
	@Override
	public String getID() {
		return "effect_break";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Break";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will break blocks and damage armor";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		World world = spell.world;
		BlockPos targetPos = spell.getData(BLOCK_HIT);
		Entity targetEntity = spell.getData(ENTITY_HIT);
		Entity caster = spell.getData(CASTER);

		double strength = 1 * getMultiplier();
		if (attributes.hasKey(Attributes.EXTEND))
			strength += Math.min(64.0, attributes.getDouble(Attributes.EXTEND));
		strength *= calcBurnoutPercent(caster);

		if (!tax(this, spell)) return false;

		if (targetEntity != null && targetEntity instanceof EntityLivingBase) {

			for (ItemStack stack : targetEntity.getArmorInventoryList()) {
				stack.damageItem((int) strength, (EntityLivingBase) targetEntity);
			}
		} else if (targetPos != null) {
			BlockPos pos = new BlockPos(targetPos);
			float hardness = world.getBlockState(pos).getBlockHardness(world, pos);
			if (hardness >= 0 && hardness < strength)
				world.destroyBlock(pos, true);
		}
		return true;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {
		World world = spell.world;
		Vec3d position = spell.getData(TARGET_HIT);

		if (position == null) return;

		LibParticles.EFFECT_REGENERATE(world, position, getPrimaryColor());
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleEffectBreak());
	}
}
