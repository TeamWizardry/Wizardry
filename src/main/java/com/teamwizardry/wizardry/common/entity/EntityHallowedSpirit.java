package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.wizardry.init.ModSounds;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Saad on 8/17/2016.
 */
public class EntityHallowedSpirit extends EntityMob {

	public EntityHallowedSpirit(World worldIn) {
		super(worldIn);
		setSize(0.6F, 1.95F);
		experienceValue = 5;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		isImmuneToFire = true;
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(1, new EntityAIWatchClosest(this, EntityPlayer.class, 50.0F));
		applyEntityAI();
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(75.0D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
		//this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.6D);
	}

	protected void applyEntityAI() {
		targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
	}

	@Override
	public void collideWithEntity(Entity entity) {
		if (getHealth() > 0) {
			if (entity.getName().equals(getName())) return;
			((EntityLivingBase) entity).motionY += 0.4;
			((EntityLivingBase) entity).attackEntityAsMob(this);
			((EntityLivingBase) entity).setRevengeTarget(this);
		}
		entity.fallDistance = 0;

		LibParticles.AIR_THROTTLE(world, getPositionVector().addVector(0, getEyeHeight(), 0), entity, Color.WHITE, Color.YELLOW, -1, false);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (world.isRemote) return;

		if ((ticksExisted % ThreadLocalRandom.current().nextInt(100, 200)) == 0)
			playSound(ModSounds.HALLOWED_SPIRIT, ThreadLocalRandom.current().nextFloat(), ThreadLocalRandom.current().nextFloat());

		fallDistance = 0;

		EntityPlayer farPlayer = world.getNearestPlayerNotCreative(this, 50);
		if (farPlayer != null) {
			Vec3d direction = getPositionVector().subtract(farPlayer.getPositionVector()).normalize();
			motionX = direction.xCoord * -0.05;
			motionY = direction.yCoord * -0.05;
			motionZ = direction.zCoord * -0.05;
			rotationYaw = (float) (((-StrictMath.atan2(direction.xCoord, direction.zCoord) * 180) / Math.PI) - 180) / 2;
		}

		EntityPlayer player = world.getNearestPlayerNotCreative(this, 2);
		EntityPlayer closePlayer = world.getNearestPlayerNotCreative(this, 10);
		boolean angry = player != null;

		if ((closePlayer != null) && !angry)
			LibParticles.HALLOWED_SPIRIT_FLAME_FAR(world, getPositionVector().addVector(0, getEyeHeight(), 0));
		else if (angry)
			LibParticles.HALLOWED_SPIRIT_FLAME_CLOSE(world, getPositionVector().addVector(0, getEyeHeight(), 0));
		else LibParticles.HALLOWED_SPIRIT_FLAME_NORMAL(world, getPositionVector().addVector(0, getEyeHeight(), 0));

		if (angry) {
			player.attackEntityFrom(DamageSource.generic, 0.15f);
			player.hurtResistantTime = 0;
		}
	}

	@Override
	public int getBrightnessForRender(float partialTicks) {
		return 255;
	}

	@Override
	public void dropLoot(boolean wasRecentlyHit, int lootingModifier, @NotNull DamageSource source) {
		super.dropLoot(wasRecentlyHit, lootingModifier, source);
	}

	@Override
	public boolean attackEntityFrom(@NotNull DamageSource source, float amount) {
		if (source.isMagicDamage()) {
			super.attackEntityFrom(source, amount);
			LibParticles.HALLOWED_SPIRIT_HURT(world, getPositionVector());
			return true;
		} else return false;
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
