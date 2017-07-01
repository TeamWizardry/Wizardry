package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.wizardry.common.entity.ai.EntityAIEntityHurtByTarget;
import com.teamwizardry.wizardry.common.entity.ai.EntityAIFollowPlayer;
import com.teamwizardry.wizardry.common.entity.ai.EntityAILivingAttack;
import com.teamwizardry.wizardry.common.entity.ai.EntityAINearestAttackableTargetFiltered;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class EntitySummonZombie extends EntityMob {

	private static final DataParameter<Boolean> ARMS_RAISED = EntityDataManager.createKey(EntityZombie.class, DataSerializers.BOOLEAN);

	private EntityLivingBase owner;
	private int time;

	public EntitySummonZombie(World worldIn) {
		super(worldIn);
		this.setSize(0.6F, 1.95F);
	}

	public EntitySummonZombie(World worldIn, EntityLivingBase owner, int time) {
		super(worldIn);
		this.setSize(0.6F, 1.95F);
		this.owner = owner;
		this.time = time;
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAILivingAttack(this, 1.0D, false));
		this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
		this.tasks.addTask(6, new EntityAIFollowPlayer(this, (EntityLiving) owner, 1.0D, 10.0F, 2.0F));
		this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
		this.applyEntityAI();
	}

	protected void applyEntityAI() {
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, EntityPigZombie.class));
		this.targetTasks.addTask(2, new EntityAIEntityHurtByTarget(this, owner));
		this.targetTasks.addTask(3, new EntityAINearestAttackableTargetFiltered<>(this, EntityPlayer.class, true, owner));
		this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(this, EntityZombie.class, true));
		this.targetTasks.addTask(5, new EntityAINearestAttackableTarget<>(this, EntitySkeleton.class, true));
		this.targetTasks.addTask(6, new EntityAINearestAttackableTarget<>(this, EntityCreeper.class, true));
		this.targetTasks.addTask(7, new EntityAINearestAttackableTarget<>(this, EntitySpider.class, true));
	}

	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(2.0D);
	}

	protected void entityInit() {
		super.entityInit();
		this.getDataManager().register(ARMS_RAISED, Boolean.FALSE);
	}

	@SideOnly(Side.CLIENT)
	public boolean isArmsRaised() {
		return this.getDataManager().get(ARMS_RAISED);
	}

	public void setArmsRaised(boolean armsRaised) {
		this.getDataManager().set(ARMS_RAISED, armsRaised);
	}

	public void notifyDataManagerChange(@NotNull DataParameter<?> key) {
		super.notifyDataManagerChange(key);
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		if (ticksExisted >= time) {
			world.removeEntity(this);
		}
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		boolean flag = super.attackEntityAsMob(entityIn);
		if (flag) {
			float f = this.world.getDifficultyForLocation(new BlockPos(this)).getAdditionalDifficulty();

			if (this.getHeldItemMainhand().isEmpty() && this.isBurning() && this.rand.nextFloat() < f * 0.3F) {
				entityIn.setFire(2 * (int) f);
			}
		}

		return flag;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_ZOMBIE_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound() {
		return SoundEvents.ENTITY_ZOMBIE_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_ZOMBIE_DEATH;
	}

	protected SoundEvent getStepSound() {
		return SoundEvents.ENTITY_ZOMBIE_STEP;
	}

	@Override
	protected void playStepSound(BlockPos pos, Block blockIn) {
		this.playSound(this.getStepSound(), 0.15F, 1.0F);
	}

	@NotNull
	@Override
	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.UNDEAD;
	}

	@Override
	public float getEyeHeight() {
		return 1.74F;
	}

	public static void registerFixesZombie(DataFixer fixer) {
		EntityLiving.registerFixesMob(fixer, EntityZombie.class);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger("time", time);
		compound.setUniqueId("owner", owner.getUniqueID());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		time = compound.getInteger("time");
		UUID uuid = compound.getUniqueId("owner");
		if (uuid != null)
			owner = world.getPlayerEntityByUUID(uuid);
	}
}
