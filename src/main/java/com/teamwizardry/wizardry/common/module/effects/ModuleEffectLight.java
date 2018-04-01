package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.module.ModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.api.util.BlockUtils;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.FACE_HIT;

/**
 * Created by Demoniaque.
 */
@RegisterModule
public class ModuleEffectLight extends ModuleEffect {

	@Nonnull
	@Override
	public String getID() {
		return "effect_light";
	}

	@Override
	public boolean run(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		BlockPos targetPos = spell.getTargetPos();
		Vec3d hit = spell.getTarget();
		EnumFacing facing = spell.getData(FACE_HIT);
		Entity caster = spell.getCaster();

		if (targetPos == null && hit != null) targetPos = new BlockPos(hit);
		if (targetPos == null) return false;

		BlockPos finalPos = null;
		if (world.isAirBlock(targetPos)) finalPos = targetPos;
		else if (facing != null && world.isAirBlock(targetPos.offset(facing))) finalPos = targetPos.offset(facing);

		if (finalPos == null) return false;
		if (!spellRing.taxCaster(spell)) return false;

		BlockUtils.placeBlock(world, finalPos, ModBlocks.LIGHT.getDefaultState(), caster instanceof EntityPlayerMP ? (EntityPlayerMP) caster : null);

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		Vec3d position = spell.getTarget();

		if (position == null) return;

		LibParticles.EXPLODE(world, position, getPrimaryColor(), getSecondaryColor(), 0.2, 0.3, 20, 40, 10, true);
	}
}
