package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.Constants.NBT;
import com.teamwizardry.wizardry.api.entity.FairyObject;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.entity.ai.FairyAIWanderAvoidWaterFlying;
import com.teamwizardry.wizardry.common.entity.ai.FairyMoveHelper;
import com.teamwizardry.wizardry.common.entity.ai.WizardryFlyablePathNavigator;
import com.teamwizardry.wizardry.common.network.PacketExplode;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityFlying;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;


/**
 * Created by Demoniaque on 8/21/2016.
 */
public class EntityFairy extends EntityTameable implements EntityFlying {

	private static final DataParameter<NBTTagCompound> DATA_FAIRY = EntityDataManager.createKey(EntityFairy.class, DataSerializers.COMPOUND_TAG);

	private EntityAIAvoidEntity<EntityPlayer> avoidEntity;

	public BlockPos originPos = null;
	public BlockPos moveTargetPos = null;
	public Vec3d lookTargetPos = null;

	private double previousDist = Double.MAX_VALUE;
	private long lastFreeTime = Long.MAX_VALUE;
	private boolean adjustingPath = false;
	private boolean moving = false;

	public EntityFairy(World worldIn) {
		super(worldIn);
		setSize(1f, 1f);

		isAirBorne = true;
		experienceValue = 5;
		moveHelper = new FairyMoveHelper(this);

		setDataFairy(new FairyObject());
	}

	public EntityFairy(World worldIn, FairyObject fairyObject) {
		super(worldIn);
		setSize(1f, 1f);
		isAirBorne = true;
		experienceValue = 5;
		moveHelper = new FairyMoveHelper(this);

		setDataFairy(fairyObject);
	}

	@Nullable
	public FairyObject getDataFairy() {
		NBTTagCompound compound = this.getDataManager().get(DATA_FAIRY);
		return FairyObject.deserialize(compound);
	}

	public void setDataFairy(FairyObject fairy) {
		if (fairy == null) return;

		this.getDataManager().set(DATA_FAIRY, fairy.serializeNBT());
		this.getDataManager().setDirty(DATA_FAIRY);
	}

	@Nullable
	@Override
	public EntityAgeable createChild(@NotNull EntityAgeable ageable) {
		return null;
	}

	@Override
	public boolean getCanSpawnHere() {
		return true;
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return false;
	}

	@Override
	public boolean canMateWith(@NotNull EntityAnimal otherAnimal) {
		return false;
	}

	@Override
	public boolean attackEntityAsMob(@NotNull Entity entityIn) {
		if (entityIn instanceof EntityUnicorn) return false;
		if (!isTamed()) return false;
		return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 3.0F);
	}

	@Override
	public boolean attackEntityFrom(@NotNull DamageSource source, float amount) {
		if (this.isEntityInvulnerable(source)) {
			return false;
		} else {
			if (this.aiSit != null) {
				this.aiSit.setSitting(false);
			}

			return super.attackEntityFrom(source, amount);
		}
	}

	public boolean isFlying() {
		return !this.onGround;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.getDataManager().register(DATA_FAIRY, new FairyObject().serializeNBT());
	}

	@Override
	public void initEntityAI() {
		FairyObject dataFairy = getDataFairy();
		if (dataFairy != null && dataFairy.isDepressed) return;

		this.tasks.addTask(1, new EntityAIFollowOwnerFlying(this, 1.0D, 5.0F, 1.0F));
		this.tasks.addTask(2, new FairyAIWanderAvoidWaterFlying(this, 1.0D));
		this.tasks.addTask(3, new EntityAIFollow(this, 1.0D, 3.0F, 7.0F));

	}

	@Override
	protected void setupTamedAI() {
		FairyObject dataFairy = getDataFairy();
		if (dataFairy != null && dataFairy.isDepressed) return;

		if (this.avoidEntity == null) {
			this.avoidEntity = new EntityAIAvoidEntity<>(this, EntityPlayer.class, 16.0F, 2, 3);
		}

		this.tasks.removeTask(this.avoidEntity);

		if (!this.isTamed()) {
			this.tasks.addTask(0, this.avoidEntity);
		}

		this.tasks.addTask(0, new EntityAIAvoidEntity<>(this, EntityUnicorn.class, 16, 2, 3));
		this.tasks.addTask(0, new EntityAIAvoidEntity<>(this, EntitySpiritWight.class, 16, 2, 3));

	}

	@Override
	public void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1.0D);
		this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(RandUtil.nextDouble(2, 3));
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(1);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64);
	}

	@NotNull
	@Override
	public PathNavigate createNavigator(@NotNull World worldIn) {
		return new WizardryFlyablePathNavigator(this, worldIn);
	}

	@Override
	public void collideWithEntity(Entity entity) {
		if (getHealth() > 0) {
			if (entity.getName().equals(getName())) return;
			((EntityLivingBase) entity).motionY += 0.3;
			((EntityLivingBase) entity).setRevengeTarget(this);
		}
		entity.fallDistance = 0;

		//if (entity.world.isRemote)
		//	LibParticles.AIR_THROTTLE(world, getPositionVector(), entity, primaryColor, primaryColor.brighter(), -1);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		setNoGravity(true);
		fallDistance = 0;

		FairyObject dataFairy = getDataFairy();
		float size = dataFairy != null && dataFairy.wasTamperedWith ? 0.2f : 1f;
		setScale(size);

		//	if (isAIDisabled()) return;
		if (isDead) return;

		if (dataFairy != null && dataFairy.isDepressed) {

			if (lookTargetPos != null)
				getLookHelper().setLookPosition(lookTargetPos.x, lookTargetPos.y, lookTargetPos.z, 20, 20);

			//	setNoAI(false);

			ArrayList<EntityAITasks.EntityAITaskEntry> tempTasks = new ArrayList<>(tasks.taskEntries);
			for (EntityAITasks.EntityAITaskEntry taskEntry : tempTasks) {
				tasks.removeTask(taskEntry.action);
			}

			ArrayList<EntityAITasks.EntityAITaskEntry> tempTargetTasks = new ArrayList<>(targetTasks.taskEntries);
			for (EntityAITasks.EntityAITaskEntry taskEntry : tempTargetTasks) {
				targetTasks.removeTask(taskEntry.action);
			}
//			return;
		}

		if (getNavigator().getPath() != null) {
			PathPoint pathPoint = getNavigator().getPath().getFinalPathPoint();
			if (pathPoint != null) {

				//	if (dist <= 1 * 1) {
				//		getNavigator().noPath();
				//		motionX = 0;
				//		motionY = 0;
				//		motionZ = 0;
				//		getMoveHelper().setMoveTo(moveTargetPos.getX() + 0.5, moveTargetPos.getY() + 0.5, moveTargetPos.getZ() + 0.5, 1);
				//	}
			}
		}

		if (!moving) {
			motionX = 0;
			motionY = 0;
			motionZ = 0;
			getMoveHelper().action = EntityMoveHelper.Action.WAIT;
			resetPositionToBB();

		}

		if ((dataFairy == null || !dataFairy.isDepressed) && getNavigator().noPath()) {
			getMoveHelper().setMoveTo(posX + RandUtil.nextDouble(-32, 32), posY + RandUtil.nextDouble(-32, 32), posZ + RandUtil.nextDouble(-32, 32), 2);

		} else if (moving && moveTargetPos != null && dataFairy != null && dataFairy.isDepressed && getNavigator().noPath()) {
			Vec3d target = new Vec3d(moveTargetPos.getX(), moveTargetPos.getY(), moveTargetPos.getZ()).add(0.5, 0.5, 0.5);
			double dist = target.squareDistanceTo(getPositionVector());
			if (Math.abs(dist - previousDist) < 0.01) {
				getMoveHelper().setMoveTo(RandUtil.nextDouble(-1, 1), RandUtil.nextDouble(-1, 1), RandUtil.nextDouble(-1, 1), 0.5);
				previousDist = dist;
				lastFreeTime = 5;
				adjustingPath = true;
				return;
			} else if (adjustingPath && lastFreeTime <= 0) {

				getMoveHelper().setMoveTo(moveTargetPos.getX() + 0.5, moveTargetPos.getY() + 0.5, moveTargetPos.getZ() + 0.5, 0.5);
				adjustingPath = false;
			} else if (adjustingPath) --lastFreeTime;

			if (dist < 1.3) {
				setPositionAndUpdate(moveTargetPos.getX() + 0.5, moveTargetPos.getY() + 0.5, moveTargetPos.getZ() + 0.5);
				motionX = 0;
				motionY = 0;
				motionZ = 0;
				moving = false;
			}

			previousDist = dist;
		}
	}

	public void moveTo(BlockPos pos) {
		moveTargetPos = pos;
		getMoveHelper().setMoveTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1);
		moving = true;
	}

	@NotNull
	@Override
	public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand) {
		ItemStack jar = player.getHeldItemMainhand();
		if (jar.getItem() == ModItems.JAR_ITEM && jar.getItemDamage() == 0) {
			succFairy(jar, player);
			return EnumActionResult.SUCCESS;
		}
		return super.applyPlayerInteraction(player, vec, hand);
	}

	private void succFairy(ItemStack stack, EntityPlayer player) {
		FairyObject dataFairy = getDataFairy();
		if (dataFairy == null) return;

		stack.shrink(1);
		ItemStack jar = new ItemStack(ModItems.JAR_ITEM);
		jar.setItemDamage(2);
		dataFairy.wasTamperedWith = true;
		NBTHelper.setTag(jar, "fairy", dataFairy.serializeNBT());
		player.addItemStackToInventory(jar);
		world.removeEntity(this);
	}

	@Override
	public void onDeath(@Nonnull DamageSource cause) {
		super.onDeath(cause);

		FairyObject dataFairy = getDataFairy();
		if (dataFairy == null) return;

		if (!world.isRemote && getHealth() <= 0)
			PacketHandler.NETWORK.sendToAllAround(new PacketExplode(getPositionVector().add(0, 0.25, 0), dataFairy.primaryColor, dataFairy.secondaryColor, 0.5, 0.5, RandUtil.nextInt(100, 200), 75, 25, true),
					new NetworkRegistry.TargetPoint(world.provider.getDimension(), posX, posY, posZ, 256));
	}

	@Override
	public void dropLoot(boolean wasRecentlyHit, int lootingModifier, @Nonnull DamageSource source) {
		//super.dropLoot(wasRecentlyHit, lootingModifier, source);
		FairyObject dataFairy = getDataFairy();
		if (dataFairy == null) return;

		// TODO color
		ItemStack fairyWings = new ItemStack(ModItems.FAIRY_WINGS);
		ItemStack fairyDust = new ItemStack(ModItems.FAIRY_DUST);
		NBTHelper.setInt(fairyWings, NBT.FAIRY_COLOR, dataFairy.primaryColor.getRGB());
		entityDropItem(fairyDust, RandUtil.nextFloat());
		entityDropItem(fairyWings, RandUtil.nextFloat());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);

		setDataFairy(FairyObject.deserialize(NBTHelper.getCompoundTag(compound, "fairy")));

		if (NBTHelper.hasKey(compound, "origin_x") && NBTHelper.hasKey(compound, "origin_y") && NBTHelper.hasKey(compound, "origin_z"))
			originPos = new BlockPos(NBTHelper.getInteger(compound, "origin_x"), NBTHelper.getInteger(compound, "origin_y"), NBTHelper.getInteger(compound, "origin_z"));

		if (NBTHelper.hasKey(compound, "move_target_x") && NBTHelper.hasKey(compound, "move_target_y") && NBTHelper.hasKey(compound, "move_target_z"))
			moveTargetPos = new BlockPos(NBTHelper.getInteger(compound, "move_target_x"), NBTHelper.getInteger(compound, "move_target_y"), NBTHelper.getInteger(compound, "move_target_z"));

		if (NBTHelper.hasKey(compound, "look_target_x") && NBTHelper.hasKey(compound, "look_target_y") && NBTHelper.hasKey(compound, "look_target_z"))
			lookTargetPos = new Vec3d(NBTHelper.getDouble(compound, "look_target_x"), NBTHelper.getDouble(compound, "look_target_y"), NBTHelper.getDouble(compound, "look_target_z"));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);

		FairyObject dataFairy = getDataFairy();
		if (dataFairy != null)
			NBTHelper.setCompoundTag(compound, "fairy", dataFairy.serializeNBT());

		if (originPos != null) {
			compound.setInteger("origin_x", originPos.getX());
			compound.setInteger("origin_y", originPos.getY());
			compound.setInteger("origin_z", originPos.getZ());
		}

		if (moveTargetPos != null) {
			compound.setInteger("move_target_x", moveTargetPos.getX());
			compound.setInteger("move_target_y", moveTargetPos.getY());
			compound.setInteger("move_target_z", moveTargetPos.getZ());
		}

		if (lookTargetPos != null) {
			compound.setDouble("look_target_x", lookTargetPos.x);
			compound.setDouble("look_target_y", lookTargetPos.y);
			compound.setDouble("look_target_z", lookTargetPos.z);
		}
	}
}
