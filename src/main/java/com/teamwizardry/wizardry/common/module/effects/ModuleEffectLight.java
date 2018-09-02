package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.module.ModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.api.util.BlockUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

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
		EnumFacing facing = spell.getFaceHit();
		Entity caster = spell.getCaster();

		if (!spellRing.taxCaster(spell, true)) return false;

		if (targetPos == null) return true;

		BlockPos finalPos = targetPos;
		if (facing != null && world.isAirBlock(targetPos.offset(facing))) finalPos = targetPos.offset(facing);

		BlockUtils.placeBlock(world, finalPos, ModBlocks.LIGHT.getDefaultState(), caster instanceof EntityPlayerMP ? (EntityPlayerMP) caster : null);

		world.playSound(null, targetPos, ModSounds.SPARKLE, SoundCategory.AMBIENT, 1f, 1f);

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		BlockPos position = spell.getTargetPos();
		EnumFacing facing = spell.getFaceHit();

		if (position == null) return;

		BlockPos finalPos = position;
		if (facing != null && world.isAirBlock(position.offset(facing))) finalPos = position.offset(facing);

		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFloatInOut(0.3f, 0.3f));
		glitter.enableMotionCalculation();
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(new Vec3d(finalPos).add(0.5, 0.5, 0.5)), 500, 0, (i, build) -> {
			double radius = 1;
			double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
			double r = radius * RandUtil.nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);
			build.setScaleFunction(new InterpScale(RandUtil.nextFloat(0.2f, 1f), 0f));
			build.setMotion(new Vec3d(x, RandUtil.nextDouble(-radius, radius), z).normalize().scale(RandUtil.nextFloat()));
			build.setAcceleration(Vec3d.ZERO);
			build.setLifetime(20);
			build.setDeceleration(new Vec3d(0.4, 0.4, 0.4));

			if (RandUtil.nextBoolean()) {
				build.setColorFunction(new InterpColorHSV(getPrimaryColor(), getSecondaryColor()));
			} else {
				build.setColorFunction(new InterpColorHSV(getSecondaryColor(), getPrimaryColor()));
			}
		});
	}
}
