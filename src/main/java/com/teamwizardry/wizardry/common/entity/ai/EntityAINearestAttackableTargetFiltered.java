package com.teamwizardry.wizardry.common.entity.ai;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class EntityAINearestAttackableTargetFiltered<T extends EntityLivingBase> extends EntityAITarget {
	protected final Class<T> targetClass;
	/**
	 * Instance of EntityAINearestAttackableTargetSorter.
	 */
	protected final EntityAINearestAttackableTarget.Sorter sorter;
	protected final Predicate<? super T> targetEntitySelector;
	private final int targetChance;
	protected T targetEntity;

	public EntityAINearestAttackableTargetFiltered(EntityCreature creature, Class<T> classTarget, boolean checkSight) {
		this(creature, classTarget, checkSight, false);
	}

	public EntityAINearestAttackableTargetFiltered(EntityCreature creature, Class<T> classTarget, boolean checkSight, boolean onlyNearby) {
		this(creature, classTarget, 10, checkSight, onlyNearby, null);
	}

	public EntityAINearestAttackableTargetFiltered(EntityCreature creature, Class<T> classTarget, int chance, boolean checkSight, boolean onlyNearby, @Nullable final Predicate<? super T> targetSelector) {
		super(creature, checkSight, onlyNearby);
		this.targetClass = classTarget;
		this.targetChance = chance;
		this.sorter = new EntityAINearestAttackableTarget.Sorter(creature);
		this.setMutexBits(1);

		UUID exclude = creature.getEntityData().getUniqueId("owner");

		this.targetEntitySelector = (Predicate<T>) entity -> (entity != null && !entity.getUniqueID().equals(exclude)) && (targetSelector == null || targetSelector.apply(entity) && (EntitySelectors.NOT_SPECTATING.apply(entity) && EntityAINearestAttackableTargetFiltered.this.isSuitableTarget(entity, false)));
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@SuppressWarnings("unchecked")
	public boolean shouldExecute() {
		if (this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0) {
			return false;
		} else if (this.targetClass != EntityPlayer.class && this.targetClass != EntityPlayerMP.class) {
			List<T> list = this.taskOwner.world.getEntitiesWithinAABB(this.targetClass, this.getTargetableArea(this.getTargetDistance()), this.targetEntitySelector);

			if (list.isEmpty()) {
				return false;
			} else {
				list.sort(this.sorter);
				this.targetEntity = list.get(0);
				return true;
			}
		} else {
			this.targetEntity = (T) this.taskOwner.world.getNearestAttackablePlayer(this.taskOwner.posX, this.taskOwner.posY + (double) this.taskOwner.getEyeHeight(), this.taskOwner.posZ, this.getTargetDistance(), this.getTargetDistance(), new Function<EntityPlayer, Double>() {
				@Nullable
				public Double apply(@Nullable EntityPlayer p_apply_1_) {
					ItemStack itemstack = p_apply_1_.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

					if (itemstack.getItem() == Items.SKULL) {
						int i = itemstack.getItemDamage();
						boolean flag = EntityAINearestAttackableTargetFiltered.this.taskOwner instanceof EntitySkeleton && i == 0;
						boolean flag1 = EntityAINearestAttackableTargetFiltered.this.taskOwner instanceof EntityZombie && i == 2;
						boolean flag2 = EntityAINearestAttackableTargetFiltered.this.taskOwner instanceof EntityCreeper && i == 4;

						if (flag || flag1 || flag2) {
							return Double.valueOf(0.5D);
						}
					}

					return Double.valueOf(1.0D);
				}
			}, (Predicate<EntityPlayer>) this.targetEntitySelector);
			return this.targetEntity != null;
		}
	}

	protected AxisAlignedBB getTargetableArea(double targetDistance) {
		return this.taskOwner.getEntityBoundingBox().grow(targetDistance, 4.0D, targetDistance);
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting() {
		this.taskOwner.setAttackTarget(this.targetEntity);
		super.startExecuting();
	}

}
