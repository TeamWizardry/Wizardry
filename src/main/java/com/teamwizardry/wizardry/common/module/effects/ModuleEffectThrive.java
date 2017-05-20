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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectThrive extends Module {

	@Nonnull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Blocks.BONE_BLOCK);
	}

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@Nonnull
	@Override
	public String getID() {
		return "thrive";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Thrive";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will heal entities & speed up plant growth";
	}

	@Override
	public double getManaDrain() {
		return 100;
	}

	@Override
	public double getBurnoutFill() {
		return 500;
	}

	@Nullable
	@Override
	public Color getPrimaryColor() {
		return Color.RED;
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		World world = spell.world;
		BlockPos targetPos = spell.getData(BLOCK_HIT);
		Entity targetEntity = spell.getData(ENTITY_HIT);
		Entity caster = spell.getData(CASTER);

		if (targetEntity instanceof EntityLivingBase) {
			double strength = 0.3;
			if (attributes.hasKey(Attributes.EXTEND))
				strength += Math.min(20.0 / 10.0, attributes.getDouble(Attributes.EXTEND) / 10.0);
			strength *= calcBurnoutPercent(caster);

			((EntityLivingBase) targetEntity).setHealth((float) (((EntityLivingBase) targetEntity).getHealth() + strength));
		}
		if (targetPos != null) {
			BlockPos pos = new BlockPos(targetPos);
			if (world.getBlockState(pos).getBlock() instanceof IGrowable)
				ItemDye.applyBonemeal(new ItemStack(Items.DYE), world, pos);
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
		return cloneModule(new ModuleEffectThrive());
	}
}
