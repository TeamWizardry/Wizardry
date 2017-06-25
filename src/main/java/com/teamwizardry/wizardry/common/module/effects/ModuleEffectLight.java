package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.api.util.BlockUtils;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
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
		return "effect_light";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Light";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will place a magical light source at the target location";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		World world = spell.world;
		BlockPos targetPos = spell.getData(BLOCK_HIT);
		Vec3d hit = spell.getData(TARGET_HIT);
		Entity targetEntity = spell.getData(ENTITY_HIT);
		EnumFacing facing = spell.getData(FACE_HIT);
		Entity caster = spell.getData(CASTER);

		if (targetPos == null && hit != null) targetPos = new BlockPos(hit);
		if (targetPos == null) return false;

		BlockUtils.placeBlock(world, world.isAirBlock(targetPos) ? targetPos : facing != null ? targetPos.offset(facing) : targetPos, ModBlocks.LIGHT.getDefaultState(), caster instanceof EntityPlayerMP ? (EntityPlayerMP) caster : null);

		return true;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {
		World world = spell.world;
		Vec3d position = spell.getData(TARGET_HIT);

		if (position == null) return;

		LibParticles.EXPLODE(world, position, getPrimaryColor(), getSecondaryColor(), 0.2, 0.3, 20, 40, 10, true);
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleEffectLight());
	}
}
