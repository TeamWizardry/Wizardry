package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.attribute.Attributes;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import com.teamwizardry.wizardry.common.entity.EntityBackupZombie;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectBackup extends ModuleEffect {

	@Nonnull
	@Override
	public String getID() {
		return "effect_backup";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Backup";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will summon undead mobs to help you out in battle";
	}

	@Override
	@SuppressWarnings("unused")
	public boolean run(@Nonnull SpellData spell) {
		World world = spell.world;
		Entity targetEntity = spell.getData(ENTITY_HIT);
		Vec3d targetPos = spell.getData(TARGET_HIT);
		EnumFacing facing = spell.getData(FACE_HIT);
		Entity caster = spell.getData(CASTER);

		double range = getModifier(spell, Attributes.AREA, 1, 16) / 2.0;
		double time = getModifier(spell, Attributes.DURATION, 500, 1000);

		if (!tax(this, spell)) return false;

		if (targetPos == null) return true;
		if (!(caster instanceof EntityLivingBase)) return true;
		if (facing != null && !world.isAirBlock(new BlockPos(targetPos))) {
			targetPos = new Vec3d(new BlockPos(targetPos).offset(facing)).addVector(0.5, 0.5, 0.5);
		}

		EntityBackupZombie zombie = new EntityBackupZombie(world, (EntityLivingBase) caster, (int) time);
		zombie.setPosition(targetPos.x, targetPos.y, targetPos.z);
		zombie.forceSpawn = true;
		world.spawnEntity(zombie);

		return true;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {
		World world = spell.world;
		Vec3d position = spell.getData(TARGET_HIT);

		if (position == null) return;

		ParticleBuilder glitter = new ParticleBuilder(1);
		glitter.setAlphaFunction(new InterpFadeInOut(0.0f, 0.1f));
		glitter.setColorFunction(new InterpColorHSV(getPrimaryColor(), getSecondaryColor()));
		glitter.enableMotionCalculation();
		glitter.setScaleFunction(new InterpScale(1, 0));
		glitter.setAcceleration(new Vec3d(0, -0.05, 0));
		glitter.setCollision(true);
		glitter.setCanBounce(true);
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(position), RandUtil.nextInt(20, 30), 0, (aFloat, particleBuilder) -> {
			if (RandUtil.nextInt(5) == 0) {
				glitter.setRenderNormalLayer(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			} else {
				glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			}

			glitter.setScale(RandUtil.nextFloat());
			glitter.setLifetime(RandUtil.nextInt(50, 100));
			glitter.addMotion(new Vec3d(RandUtil.nextDouble(-0.05, 0.05), RandUtil.nextDouble(0.01, 0.05), RandUtil.nextDouble(-0.05, 0.05)));
		});

	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleEffectBackup());
	}
}
