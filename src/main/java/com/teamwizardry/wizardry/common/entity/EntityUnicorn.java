package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.entity.ai.EntityAIUnicornWander;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIRunAroundLikeCrazy;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityUnicorn extends EntityHorse {

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
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		flatulenceTicker = RandUtil.nextInt(20, 200);
	}

	@Override
	protected void initEntityAI() {
		//this.tasks.addTask(0, new EntityAIUnicornCharge(this, 1.0F, 10.0F, 5.0));
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIMoveTowardsRestriction(this, 1.0D));
		this.tasks.addTask(3, new EntityAIUnicornWander(this, 1.0D));
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
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
//		if (world.isRemote) return;
//
//		EntityLivingBase target = this.getAttackTarget();
//		if (target == null) {
//			this.setAttackTarget(world.getNearestAttackablePlayer(this, 50, 50));
//		}// else if (target instanceof EntityPlayer && (((EntityPlayer) target).isCreative() || ((EntityPlayer) target).isSpectator())) {
//		//	target = null;
//		//}
//		//if (target == null) return;
//
//		Vec3d look;
//		if (target != null) {
//			look = target.getPositionVector().subtract(getPositionVector()).normalize();
//			look = look.crossProduct(new Vec3d(0, 1, 0)).crossProduct(look);
//		} else {
//			look = new Vec3d(0, 1, 0);
//		}
//
//		double pitch = Math.asin(look.y / look.lengthVector());
//		Minecraft.getMinecraft().player.sendChatMessage(pitch + "");
//		if (pitch >= 1 || pitch < 0.5) {
//			look = new Vec3d(0, 1, 0);
//		}
//		List<Vec3d> circle2 = new InterpCircle(getPositionVector().addVector(0, -1, 0), look, 3).list(20);
//		List<Vec3d> circle3 = new InterpCircle(getPositionVector().addVector(0, -1, 0), look, 2).list(20);
//		List<Vec3d> circle4 = new InterpCircle(getPositionVector().addVector(0, -1, 0), look, 1).list(20);
//
//		List<Vec3d> finalList = new ArrayList<>();
//		finalList.addAll(circle2);
//		finalList.addAll(circle3);
//		finalList.addAll(circle4);
//
//		for (Vec3d vec3d : finalList) {
//			BlockPos pos = new BlockPos(vec3d);
//			if (!world.isAirBlock(pos)) {
//				TileEntity tile = world.getTileEntity(pos);
//				if (tile != null && tile instanceof TileUnicornTrail) {
//					((TileUnicornTrail) tile).savedTime = System.currentTimeMillis();
//				}
//				continue;
//			}
//			if (!world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos)).isEmpty()) continue;
//
//			world.setBlockState(pos, ModBlocks.UNICORN_TRAIL.getDefaultState());
//		}
//
//		if (target == null) return;
//
//		//if (getNavigator().noPath()) {
//		getNavigator().tryMoveToXYZ(target.posX, target.posY, target.posZ, 1);
//		//}
//
//		if (getEntityBoundingBox().intersects(target.getEntityBoundingBox())) {
//			target.knockBack(this, 2F, MathHelper.sin(rotationYaw), -MathHelper.cos(rotationYaw));
//			knockBack(this, 1F, -MathHelper.sin(rotationYaw), MathHelper.cos(rotationYaw));
//			target.attackEntityFrom(DamageSource.causeMobDamage(target), (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
//			getNavigator().setPath(null, 1);
//			givenPath = false;
//		}
//		/*
//		if (!isCharging) {
//			isCharging = true;
//			prepareChargeTicks = 0;
//		}
//
//		if (prepareChargeTicks < 60) {
//			prepareChargeTicks++;
//			limbSwingAmount += prepareChargeTicks / 10;
//		} else {
//			if (getNavigator().noPath() && !givenPath) {
//				Vec3d excess = target.getPositionVector();
//				getNavigator().tryMoveToXYZ(excess.x, excess.y, excess.z, 2);
//				givenPath = true;
//			}
//
//			if (getEntityBoundingBox().grow(1, 1, 1).intersects(target.getEntityBoundingBox())) {
//				target.knockBack(this, 3F, MathHelper.sin(rotationYaw), -MathHelper.cos(rotationYaw));
//				knockBack(this, 1F, -MathHelper.sin(rotationYaw), MathHelper.cos(rotationYaw));
//				target.attackEntityFrom(DamageSource.causeMobDamage(target), (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
//				isCharging = false;
//				getNavigator().setPath(null, 1);
//				givenPath = false;
//			}
//		}
//		*/
	}

	@SuppressWarnings({ "unused" })
	private void fart() {
		flatulenceTicker--;
		if (flatulenceTicker <= 0) {
			Vec3d fartPos = this.getLookVec().subtract(this.getPositionVector()).rotateYaw((float) Math.PI);
			for (int p = 0; p < 64; p++) {
				this.world.spawnParticle(EnumParticleTypes.REDSTONE, fartPos.x + RandUtil.nextDouble(-FART_SIZE, FART_SIZE),
						this.posY + this.getEyeHeight() - FART_OFFSET + RandUtil.nextDouble(-FART_SIZE, FART_SIZE),
						fartPos.z + RandUtil.nextDouble(-FART_SIZE, FART_SIZE),
						this.world.rand.nextFloat(), this.world.rand.nextFloat(), this.world.rand.nextFloat()
				);
			}
			flatulenceTicker = RandUtil.nextInt(200, 6000);
		}
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
