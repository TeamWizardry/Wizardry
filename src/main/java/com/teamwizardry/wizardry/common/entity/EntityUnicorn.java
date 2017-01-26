package com.teamwizardry.wizardry.common.entity;

import java.util.concurrent.ThreadLocalRandom;

import com.teamwizardry.wizardry.common.entity.ai.EntityAICharge;
import com.teamwizardry.wizardry.common.entity.ai.EntityAIRainbowShield;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityUnicorn extends EntityMob 
{
    	public static final String CHARGE_COOLDOWN = "charge_cooldown";
	public static final String SHIELD_COOLDOWN = "shield_cooldown";
	public static final String CONSUMED = "consumed";
	
	private static final float FART_OFFSET = 0.35F;
	private static final float FART_SIZE = 0.5F;
	//The range at which the unicorn switches from defensive mode to offensive mode, or vice versa
	private static final float ATK_MODE_SWITCH = 10.0F;

	private int chargeCooldown = 0;
	private int shieldCooldown = 0;
	//Time in ticks until the next fart
	private int flatulenceTicker;

	public EntityUnicorn(World worldIn) {
		super(worldIn);
		this.setSize(1.4F, 1.6F);
		this.stepHeight = 1.0F;
		this.tasks.addTask(1, new EntityAIWander(this, 0.5F));
		this.tasks.addTask(2, new EntityAICharge(this, 2.0F, ATK_MODE_SWITCH));
		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<EntityPig>(this, EntityPig.class, true, false));
		this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
	}
	
	@Override
	protected void entityInit() 
	{
		super.entityInit();	
		flatulenceTicker = ThreadLocalRandom.current().nextInt(20, 200);
	}
	
	@Override
	protected void applyEntityAttributes() 
	{
		super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
	}

	@Override
	public void onLivingUpdate() 
	{
		super.onLivingUpdate();
		chargeCooldown--;
		shieldCooldown--;
		this.fart();
	}
	
	private void fart() {
		flatulenceTicker--;
		if(flatulenceTicker <= 0) {
			Vec3d fartPos = this.getLookVec().subtract(this.getPositionVector()).rotateYaw((float) Math.PI);
			for(int p = 0; p < 64; p++) {
				this.world.spawnParticle(EnumParticleTypes.REDSTONE, fartPos.xCoord + ThreadLocalRandom.current().nextDouble(-FART_SIZE, FART_SIZE),
						this.posY + this.getEyeHeight() - FART_OFFSET + ThreadLocalRandom.current().nextDouble(-FART_SIZE, FART_SIZE),
						fartPos.zCoord + ThreadLocalRandom.current().nextDouble(-FART_SIZE, FART_SIZE),
						this.world.rand.nextFloat(), this.world.rand.nextFloat(), this.world.rand.nextFloat()
						);
			}
			flatulenceTicker = ThreadLocalRandom.current().nextInt(200, 6000);
		}
	}
	
	public int getChargeCooldown() {
		return chargeCooldown;
	}

	public void resetChargeCooldown() {
		chargeCooldown = this.world.rand.nextInt(60) + 20;
	}

	public int getShieldCooldown() {
		return shieldCooldown;
	}
	
	public void resetShieldCooldown() {
		shieldCooldown = this.world.rand.nextInt(60) + 20;
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
	    super.writeEntityToNBT(compound);
	    compound.setInteger(CHARGE_COOLDOWN, chargeCooldown);
	    compound.setInteger(SHIELD_COOLDOWN, shieldCooldown);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
	    super.readEntityFromNBT(compound);
	    chargeCooldown = compound.getInteger(CHARGE_COOLDOWN);
	    shieldCooldown = compound.getInteger(SHIELD_COOLDOWN);
	}
}