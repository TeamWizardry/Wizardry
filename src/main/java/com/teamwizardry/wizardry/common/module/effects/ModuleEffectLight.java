package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
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

		world.playSound(null, targetPos, ModSounds.SPARKLE, SoundCategory.AMBIENT, 1f, 1f);

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		BlockPos position = spell.getTargetPos();

		if (position == null) return;

		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));
		glitter.enableMotionCalculation();
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(new Vec3d(position).addVector(0.5, 0.5, 0.5)), 500, 0, (i, build) -> {
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
