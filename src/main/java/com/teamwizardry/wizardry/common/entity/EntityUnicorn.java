package com.teamwizardry.wizardry.common.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.concurrent.ThreadLocalRandom;

public class EntityUnicorn extends EntityHorse {

	public static final String CHARGE_COOLDOWN = "charge_cooldown";
	public static final String SHIELD_COOLDOWN = "shield_cooldown";

	private static final float FART_OFFSET = 0.35F;
	private static final float FART_SIZE = 0.5F;

	public boolean isCharging = false;
	public int prepareChargeTicks = 0;
	public int shieldCooldown = 0;
	public int flatulenceTicker;
	public boolean givenPath = false;
	private EntityLivingBase target;

	public EntityUnicorn(World worldIn) {
		super(worldIn);
		this.setSize(1.3964844F, 1.6F);
		this.isImmuneToFire = false;
		this.setChested(false);
		this.stepHeight = 1.0F;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		flatulenceTicker = ThreadLocalRandom.current().nextInt(20, 200);
	}

	@Override
	protected void initEntityAI() {
		//this.tasks.addTask(0, new EntityAIUnicornCharge(this, 1.0F, 10.0F, 5.0));
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIMoveTowardsRestriction(this, 1.0D));
		this.tasks.addTask(3, new EntityAIWander(this, 1.0D));
		this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(5, new EntityAILookIdle(this));
		this.tasks.addTask(6, new EntityAIRunAroundLikeCrazy(this, 1.2D));
		this.tasks.addTask(7, new EntityAIMate(this, 1.0D));
		this.tasks.addTask(8, new EntityAIFollowParent(this, 1.0D));

		//this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, false, false));
		this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		shieldCooldown--;
		this.fart();

		if (target == null) {
			target = world.getNearestAttackablePlayer(this, 50, 50);
		}
		if (target == null) return;
		if (!target.isEntityAlive()) return;
		if (target.getDistanceToEntity(this) > 10) return;
		if (((EntityPlayer) target).capabilities.isCreativeMode || ((EntityPlayer) target).isSpectator())
			return;

		if (!isCharging) {
			isCharging = true;
			prepareChargeTicks = 0;
		}

		if (prepareChargeTicks < 60) {
			prepareChargeTicks++;
			limbSwingAmount += prepareChargeTicks / 10;
		} else {
			if (getNavigator().noPath() && !givenPath) {
				Vec3d excess = target.getPositionVector();
				getNavigator().tryMoveToXYZ(excess.xCoord, excess.yCoord, excess.zCoord, 2);
				givenPath = true;
			}

			if (getEntityBoundingBox().expand(1, 1, 1).intersectsWith(target.getEntityBoundingBox())) {
				target.knockBack(this, 3F, MathHelper.sin(rotationYaw), -MathHelper.cos(rotationYaw));
				knockBack(this, 1F, -MathHelper.sin(rotationYaw), MathHelper.cos(rotationYaw));
				target.attackEntityFrom(DamageSource.causeMobDamage(target), (float) 5);
				isCharging = false;
				getNavigator().setPath(null, 1);
				givenPath = false;
			}
		}
	}

	private void fart() {
		flatulenceTicker--;
		if (flatulenceTicker <= 0) {
			Vec3d fartPos = this.getLookVec().subtract(this.getPositionVector()).rotateYaw((float) Math.PI);
			for (int p = 0; p < 64; p++) {
				this.world.spawnParticle(EnumParticleTypes.REDSTONE, fartPos.xCoord + ThreadLocalRandom.current().nextDouble(-FART_SIZE, FART_SIZE),
						this.posY + this.getEyeHeight() - FART_OFFSET + ThreadLocalRandom.current().nextDouble(-FART_SIZE, FART_SIZE),
						fartPos.zCoord + ThreadLocalRandom.current().nextDouble(-FART_SIZE, FART_SIZE),
						this.world.rand.nextFloat(), this.world.rand.nextFloat(), this.world.rand.nextFloat()
				);
			}
			flatulenceTicker = ThreadLocalRandom.current().nextInt(200, 6000);
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger(SHIELD_COOLDOWN, shieldCooldown);
		compound.setBoolean("is_charging", isCharging);
		compound.setInteger("prepare_charge_ticks", prepareChargeTicks);
		compound.setInteger("target", target.getEntityId());
		compound.setBoolean("given_path", givenPath);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		shieldCooldown = compound.getInteger(SHIELD_COOLDOWN);
		isCharging = compound.getBoolean("is_charging");
		prepareChargeTicks = compound.getInteger("prepare_charge_ticks");
		givenPath = compound.getBoolean("given_path");
		target = (EntityLivingBase) world.getEntityByID(compound.getInteger("target"));
	}
}
