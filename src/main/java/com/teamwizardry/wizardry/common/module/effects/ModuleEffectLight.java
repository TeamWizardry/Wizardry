package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
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
public class ModuleEffectLight extends ModuleEffect {

	@Nonnull
	@Override
	public String getID() {
		return "effect_thrive";
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
	public boolean run(@Nonnull SpellData spell) {
		World world = spell.world;
		BlockPos targetPos = spell.getData(BLOCK_HIT);
		Entity targetEntity = spell.getData(ENTITY_HIT);
		Entity caster = spell.getData(CASTER);

		// TODO

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
		return cloneModule(new ModuleEffectLight());
	}
}
