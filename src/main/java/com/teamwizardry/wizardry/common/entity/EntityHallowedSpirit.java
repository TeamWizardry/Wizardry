package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.wizardry.client.fx.GlitterFactory;
import com.teamwizardry.wizardry.client.fx.particle.SparkleFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Saad on 8/17/2016.
 */
public class EntityHallowedSpirit extends EntityMob {

	private boolean angry = false;

	public EntityHallowedSpirit(World worldIn) {
		super(worldIn);
		this.setSize(0.6F, 1.95F);
		this.experienceValue = 5;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.isImmuneToFire = true;
	}

	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIAttackMelee(this, 0.3, true));
		this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.3D));
		this.tasks.addTask(7, new EntityAIWander(this, 0.3D));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.applyEntityAI();
	}

	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(50.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
		//this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.6D);
	}

	protected void applyEntityAI() {
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
	}

	@Override
	public void collideWithEntity(Entity entity) {
		if (this.getHealth() > 0) {
			if (entity.getName().equals(getName())) return;
			((EntityLivingBase) entity).motionY += 0.3;
			((EntityLivingBase) entity).attackEntityAsMob(this);
			((EntityLivingBase) entity).setRevengeTarget(this);
		}
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (getAttackTarget() != null) {
			EntityLivingBase target = getAttackTarget();
			if (target.getDistanceSq(getPosition()) <= 2) {
				angry = true;
			}
		}

		for (int i = 0; i < 5; i++) {
			Vec3d headCenter = new Vec3d(posX + width + ThreadLocalRandom.current().nextDouble(-0.3, 0.3), getEyeHeight() + ThreadLocalRandom.current().nextDouble(-0.3, 0.3), posZ + width + ThreadLocalRandom.current().nextDouble(-0.3, 0.3));
			SparkleFX headLight = GlitterFactory.getInstance().createSparkle(worldObj, headCenter, 30);
			if (this.getAttackTarget() != null) {
				headLight.setColor(Color.RED);
				headLight.setRandomlyShiftColor(-0.2, 0.2, true, false, false);
			}
			if (!angry)
				headLight.setMotion(ThreadLocalRandom.current().nextDouble(0.005, 0.01), ThreadLocalRandom.current().nextDouble(0.005, 0.01), ThreadLocalRandom.current().nextDouble(0.005, 0.01));
			else {
				headLight.setMotion(ThreadLocalRandom.current().nextDouble(0.1, 0.2), 0, ThreadLocalRandom.current().nextDouble(0.1, 0.2));
				headLight.setJitter(10, 0, 0.1, 0);
			}
			headLight.setAlpha(0.4f);
			headLight.setScale(0.5f);
			headLight.setBlurred();
			headLight.setShrink();
			headLight.setFadeOut();
		}
	}

	@Override
	public int getBrightnessForRender(float partialTicks) {
		return 255;
	}

	@Override
	public void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
		super.dropLoot(wasRecentlyHit, lootingModifier, source);
		if (!getEntityWorld().isRemote) {
			/*for (int i = 0; i < 1 + lootingModifier; i++) {
				if (rand.nextInt(2) == 0) {
					getEntityWorld().spawnEntityInWorld(new EntityItem(getEntityWorld(), posX, posY + 0.5, posZ, new ItemStack(Items.BONE, 1)));
				}
			}
			for (int i = 0; i < 1 + lootingModifier; i++) {
				if (rand.nextInt(3) == 0) {
					getEntityWorld().spawnEntityInWorld(new EntityItem(getEntityWorld(), posX, posY + 0.5, posZ, new ItemStack(MainRegistry.impTallow, 1)));
				}
			}*/
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		float initHealth = getHealth();
		if (getHealth() <= 0 && initHealth > 0) {
			if (source.getEntity() instanceof EntityPlayer) {

				//if (((EntityPlayer) source.getEntity()).hasCapability(ImpurityProvider.impurityCapability, null)) {
				//	source.getEntity().getCapability(ImpurityProvider.impurityCapability, null).setImpurity((EntityPlayer) source.getEntity(), ((EntityPlayer) source.getEntity()).getCapability(ImpurityProvider.impurityCapability, null).getImpurity() + rand.nextInt(3) + 3);
				//}
			}
		}
		return true;
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
