package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad.
 */
public class EntityJumpPad extends EntityLiving {

	public EntityJumpPad(World worldIn) {
		super(worldIn);
		setSize(1.1F, 1.1F);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
	}

	@Override
	public void collideWithEntity(Entity entity) {
		if (!(entity instanceof EntityLivingBase)) return;
		((EntityLivingBase) entity).motionY += 0.4;
		entity.fallDistance = 0;
		Color color1 = new Color(
				ThreadLocalRandom.current().nextInt(100, 255),
				ThreadLocalRandom.current().nextInt(100, 255),
				ThreadLocalRandom.current().nextInt(100, 255),
				ThreadLocalRandom.current().nextInt(100, 255));
		Color color2 = new Color(
				ThreadLocalRandom.current().nextInt(100, 255),
				ThreadLocalRandom.current().nextInt(100, 255),
				ThreadLocalRandom.current().nextInt(100, 255),
				ThreadLocalRandom.current().nextInt(100, 255));
		LibParticles.AIR_THROTTLE(world, entity.getPositionVector(), entity, color1, color2, 0.5, true);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (ticksExisted > 100) setDead();
		ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(30, 50));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.9f, 0.9f));
		glitter.enableMotionCalculation();
		Color color1 = new Color(
				ThreadLocalRandom.current().nextInt(100, 255),
				ThreadLocalRandom.current().nextInt(100, 255),
				ThreadLocalRandom.current().nextInt(100, 255),
				ThreadLocalRandom.current().nextInt(100, 255));
		Color color2 = new Color(
				ThreadLocalRandom.current().nextInt(100, 255),
				ThreadLocalRandom.current().nextInt(100, 255),
				ThreadLocalRandom.current().nextInt(100, 255),
				ThreadLocalRandom.current().nextInt(100, 255));
		glitter.setCollision(true);
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(getPositionVector()), 1, 1, (i, build) -> {
			if (ThreadLocalRandom.current().nextBoolean()) glitter.setColor(color1);
			else glitter.setColor(color2);
			double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
			double r = 1 * ThreadLocalRandom.current().nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);
			glitter.setPositionOffset(new Vec3d(x, 0.3, z));
			if (ThreadLocalRandom.current().nextBoolean())
				glitter.setMotion(new Vec3d(0, ThreadLocalRandom.current().nextDouble(0.2), 0));
		});
		if (ThreadLocalRandom.current().nextInt(30) == 0)
			LibParticles.AIR_THROTTLE(world, getPositionVector(), new Vec3d(0, ThreadLocalRandom.current().nextDouble(0.5), 0), color1, color2, 0.5, true);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
	}
}
