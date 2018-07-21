package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIRunAroundLikeCrazy;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityFlyHelper;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityFlying;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class EntityUnicorn extends AbstractHorse implements EntityFlying {

	private static final String SHIELD_COOLDOWN = "shield_cooldown";
	private static final String IS_CHARGING = "is_charging";
	private static final String PREPARE_CHARGE_TICKS = "prepare_charge_ticks";
	private static final String GIVEN_PATH = "given";

	private static final float FART_OFFSET = 0.35F;
	private static final float FART_SIZE = 0.5F;

	public boolean isCharging = false;
	public int prepareChargeTicks = 0;
	public int shieldCooldown = 0;
	public int flatulenceTicker;
	public boolean givenPath = false;

	public EntityUnicorn(World worldIn) {
		super(worldIn);
		moveHelper = new EntityFlyHelper(this);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		flatulenceTicker = RandUtil.nextInt(20, 200);
	}

	@NotNull
	@Override
	protected PathNavigate createNavigator(@NotNull World worldIn) {
		PathNavigateFlying navigateFlying = new PathNavigateFlying(this, world);
		navigateFlying.setCanFloat(true);
		navigateFlying.setCanEnterDoors(true);
		return navigateFlying;
	}

	@Override
	protected void initEntityAI() {
//		this.tasks.addTask(0, new EntityAIUnicornCharge(this, 1.0F, 10.0F, 5.0));
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIMoveTowardsRestriction(this, 1.0D));
//		this.tasks.addTask(3, new EntityAIUnicornWander(this, 1.0D));

//		this.tasks.addTask(3, new EntityAIWander(this, 0.6));
		this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(5, new EntityAILookIdle(this));

		this.tasks.addTask(6, new EntityAIRunAroundLikeCrazy(this, 1.2D));
		this.tasks.addTask(7, new EntityAIMate(this, 1.0D));
		this.tasks.addTask(8, new EntityAIFollowParent(this, 1.0D));

//		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, false, false));
//		this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));

		this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, false));
//		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityFairy.class, false));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
		this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(0.5);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (isAIDisabled() || isDead) return;
		if (world.isRemote) return;
		fallDistance = 0;

		if (getAttackTarget() != null) {
			moveHelper.setMoveTo(getAttackTarget().posX, getAttackTarget().posY, getAttackTarget().posZ, 1);
		}

	}

//	private void fart() {
//		flatulenceTicker--;
//		if (flatulenceTicker <= 0) {
//			Vec3d fartPos = this.getLookVec().subtract(this.getPositionVector()).rotateYaw((float) Math.PI);
//			for (int p = 0; p < 64; p++) {
//				this.world.spawnParticle(EnumParticleTypes.REDSTONE, fartPos.x + RandUtil.nextDouble(-FART_SIZE, FART_SIZE),
//						this.posY + this.getEyeHeight() - FART_OFFSET + RandUtil.nextDouble(-FART_SIZE, FART_SIZE),
//						fartPos.z + RandUtil.nextDouble(-FART_SIZE, FART_SIZE),
//						this.world.rand.nextFloat(), this.world.rand.nextFloat(), this.world.rand.nextFloat()
//				);
//			}
//			flatulenceTicker = RandUtil.nextInt(200, 6000);
//		}
//	}

	@Override
	protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
		super.dropLoot(wasRecentlyHit, lootingModifier, source);
		if (RandUtil.nextInt(20) == 0)
			entityDropItem(new ItemStack(ModItems.UNICORN_HORN), RandUtil.nextFloat());
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger(SHIELD_COOLDOWN, shieldCooldown);
		compound.setBoolean(IS_CHARGING, isCharging);
		compound.setInteger(PREPARE_CHARGE_TICKS, prepareChargeTicks);
		compound.setBoolean(GIVEN_PATH, givenPath);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		shieldCooldown = compound.getInteger(SHIELD_COOLDOWN);
		isCharging = compound.getBoolean(IS_CHARGING);
		prepareChargeTicks = compound.getInteger(PREPARE_CHARGE_TICKS);
		givenPath = compound.getBoolean(GIVEN_PATH);
	}
}
