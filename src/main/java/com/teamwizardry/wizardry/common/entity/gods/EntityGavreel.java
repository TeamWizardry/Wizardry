package com.teamwizardry.wizardry.common.entity.gods;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by LordSaad.
 */
public class EntityGavreel extends EntityLiving {

	public EntityGavreel(World worldIn) {
		super(worldIn);
		setSize(0.6F, 1.8F);
		setCustomNameTag("Gavreel");
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
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50.0D);
	}

	protected void applyEntityAI() {
	}

	@Override
	public void collideWithEntity(Entity entity) {
		entity.fallDistance = 0;

		//LibParticles.AIR_THROTTLE(world, getPositionVector().addVector(0, getEyeHeight(), 0), entity, Color.WHITE, Color.YELLOW, -1);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (world.isRemote) return;

		fallDistance = 0;

	}

	@Override
	public void dropLoot(boolean wasRecentlyHit, int lootingModifier, @Nonnull DamageSource source) {
		super.dropLoot(wasRecentlyHit, lootingModifier, source);
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
