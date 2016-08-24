package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.common.util.math.interpolate.position.InterpBezier3D;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFindEntityNearestPlayer;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
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
		this.targetTasks.addTask(0, new EntityAIFindEntityNearestPlayer(this));
		this.tasks.addTask(1, new EntityAIWander(this, 0.3D));
		this.tasks.addTask(2, new EntityAISwimming(this));
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
			((EntityLivingBase) entity).motionY += 0.4;
			((EntityLivingBase) entity).attackEntityAsMob(this);
			((EntityLivingBase) entity).setRevengeTarget(this);
		}
		entity.fallDistance = 0;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (worldObj.isRemote) return;

		Vec3d pos = new Vec3d(posX, posY + getEyeHeight(), posZ);

		ParticleBuilder glitter = new ParticleBuilder(50);
		//glitter.setPositionOffset(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.2, 0.2), ThreadLocalRandom.current().nextDouble(-0.2, 0.2), ThreadLocalRandom.current().nextDouble(-0.2, 0.2)));
		EntityPlayer player = worldObj.getNearestPlayerNotCreative(this, 2);
		if (player == null) {
			glitter.setPositionFunction(new InterpBezier3D(
					new Vec3d(0, ThreadLocalRandom.current().nextDouble(-0.1, 0), 0),
					new Vec3d(ThreadLocalRandom.current().nextDouble(-0.2, 0.2), 0.5, ThreadLocalRandom.current().nextDouble(-0.2, 0.2)),
					new Vec3d(ThreadLocalRandom.current().nextDouble(-0.65, 0.65), 0, (ThreadLocalRandom.current().nextDouble(-0.65, 0.65))),
					new Vec3d(ThreadLocalRandom.current().nextDouble(-0.1, 0.1), 0, ThreadLocalRandom.current().nextDouble(-0.1, 0.1))
			));
			glitter.setColor(Color.WHITE);
			angry = false;
		} else {
			glitter.setJitter(10, new Vec3d(0.2, 0, 0.2));
			glitter.addMotion(new Vec3d(0, 0.01, 0));
			glitter.setColor(Color.RED);

			player.attackEntityFrom(DamageSource.generic, 0.15f);
			player.hurtResistantTime = 0;

			angry = true;
		}
		glitter.disableMotion();
		glitter.setRender(new ResourceLocation(Wizardry.MODID, "particles/sparkle"));

		ParticleSpawner.spawn(glitter, worldObj, new StaticInterp<>(pos), 50);
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
