package com.teamwizardry.wizardry.common.entity;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.teamwizardry.wizardry.common.entity.ai.EntityAIFollowPlayer;
import com.teamwizardry.wizardry.common.entity.ai.EntityAILivingAttack;
import com.teamwizardry.wizardry.common.entity.ai.EntityAITargetFiltered;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class EntityBackupZombie extends EntityMob {

	private static final DataParameter<Boolean> ARMS_RAISED = EntityDataManager.createKey(EntityZombie.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> TIMER = EntityDataManager.createKey(EntityZombie.class, DataSerializers.VARINT);
	private static final DataParameter<Optional<UUID>> OWNER = EntityDataManager.createKey(EntityBackupZombie.class, DataSerializers.OPTIONAL_UNIQUE_ID);

	public EntityBackupZombie(World world) {
		super(world);
		this.setSize(0.6F, 1.95F);
	}

	public EntityBackupZombie(World world, EntityLivingBase owner, int time) {
		this(world);
		setTime(time);
		setOwner(owner.getUniqueID());
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAILivingAttack(this, 1.0D, false));
		this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
		this.tasks.addTask(6, new EntityAIFollowPlayer(this, 1.0D, 10.0F, 2.0F));
		this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
		this.applyEntityAI();
	}

	private void applyEntityAI() {
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, EntityPigZombie.class));
		this.targetTasks.addTask(4, new EntityAITargetFiltered<>(this, EntityMob.class, false, new Predicate<Entity>() {
			public boolean apply(@Nullable Entity entity) {
				if (entity == null) return false;

				UUID theirOwner = null;
				Object ownerObj = entity.getDataManager().get(OWNER);
				if (ownerObj instanceof Optional && ((Optional<?>) ownerObj).get() instanceof UUID)
					theirOwner = entity.getDataManager().get(OWNER).orNull();

				return !(theirOwner != null && getDataManager().get(OWNER).isPresent() && theirOwner.equals(getDataManager().get(OWNER).get()));
			}
		}));
	}

	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(2.0D);
	}

	protected void entityInit() {
		super.entityInit();
		this.getDataManager().register(ARMS_RAISED, Boolean.FALSE);
		this.getDataManager().register(TIMER, 0);
		this.getDataManager().register(OWNER, Optional.of(UUID.randomUUID()));
	}

	@SideOnly(Side.CLIENT)
	public boolean isArmsRaised() {
		return this.getDataManager().get(ARMS_RAISED);
	}

	public void setArmsRaised(boolean armsRaised) {
		this.getDataManager().set(ARMS_RAISED, armsRaised);
	}

	public void setTime(int time) {
		this.getDataManager().set(TIMER, time);
	}

	public void setOwner(UUID owner) {
		this.getDataManager().set(OWNER, Optional.of(owner));
	}

	@Override
	public void notifyDataManagerChange(@Nonnull DataParameter<?> key) {
		super.notifyDataManagerChange(key);
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		if (world.isRemote) return;
		if (isAIDisabled()) return;

		if (ticksExisted >= getDataManager().get(TIMER)) {
			world.removeEntity(this);
		}

		UUID uuid = null;
		if (getRevengeTarget() != null) uuid = getRevengeTarget().getUniqueID();
		if (uuid != null && getDataManager().get(OWNER).isPresent() && uuid.equals(getDataManager().get(OWNER).get())) {
			setRevengeTarget(null);
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
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
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

	@Nonnull
	@Override
	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.UNDEAD;
	}

	@Override
	public float getEyeHeight() {
		return 1.74F;
	}

	@Override
	protected boolean canDropLoot() {
		return false;
	}
}
