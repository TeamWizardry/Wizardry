package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.librarianlib.features.animator.Animator;
import com.teamwizardry.librarianlib.features.animator.Easing;
import com.teamwizardry.librarianlib.features.animator.animations.Keyframe;
import com.teamwizardry.librarianlib.features.animator.animations.KeyframeAnimation;
import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.NBTConstants.NBT;
import com.teamwizardry.wizardry.api.entity.fairy.FairyData;
import com.teamwizardry.wizardry.api.entity.fairy.fairytasks.FairyTask;
import com.teamwizardry.wizardry.api.entity.fairy.fairytasks.FairyTaskManager;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.entity.ai.FairyAIWanderAvoidWaterFlying;
import com.teamwizardry.wizardry.common.entity.ai.FairyMoveHelper;
import com.teamwizardry.wizardry.common.entity.ai.WizardryFlyablePathNavigator;
import com.teamwizardry.wizardry.common.network.PacketExplode;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFollow;
import net.minecraft.entity.ai.EntityAIFollowOwnerFlying;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityFlying;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
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
	private static final Animator ANIMATOR = new Animator();
	private static final DataParameter<ItemStack> DATA_HELD_ITEM = EntityDataManager.createKey(EntityFairy.class, DataSerializers.ITEM_STACK);

	private static final DataParameter<NBTTagCompound> DATA_FAIRY = EntityDataManager.createKey(EntityFairy.class, DataSerializers.COMPOUND_TAG);

	private static final DataParameter<NBTTagCompound> DATA_LOOK_TARGET = EntityDataManager.createKey(EntityFairy.class, DataSerializers.COMPOUND_TAG);
	private static final DataParameter<BlockPos> DATA_ORIGIN = EntityDataManager.createKey(EntityFairy.class, DataSerializers.BLOCK_POS);

	static {
		ANIMATOR.setUseWorldTicks(true);
	}

	private EntityAIAvoidEntity<EntityPlayer> avoidEntity;

	public BlockPos moveTargetPos = null;

	private double previousDist = Double.MAX_VALUE;
	private long lastFreeTime = Long.MAX_VALUE;
	private boolean adjustingPath = false;
	private boolean moving = false;

	public Vec3d animatingPos = Vec3d.ZERO;
	private Path path = null;
	@Nullable
	private FairyTask fairyTask = null;

	public EntityFairy(World worldIn) {
		super(worldIn);
		setSize(1f, 1f);

		isAirBorne = true;
		experienceValue = 5;
		moveHelper = new FairyMoveHelper(this);

		setDataFairy(new FairyData());
		setLookTarget(null);
	}

	public EntityFairy(World worldIn, FairyData fairyData) {
		super(worldIn);
		setSize(1f, 1f);
		isAirBorne = true;
		experienceValue = 5;
		moveHelper = new FairyMoveHelper(this);

		setDataFairy(fairyData);
		setLookTarget(null);

		if (fairyData.isDepressed) setEntityInvulnerable(true);
	}

	@Nullable
	public Vec3d getLookTarget() {
		NBTTagCompound compound = this.getDataManager().get(DATA_LOOK_TARGET);

		if (NBTHelper.hasKey(compound, "look_target_x") && NBTHelper.hasKey(compound, "look_target_y") && NBTHelper.hasKey(compound, "look_target_z"))
			return new Vec3d(NBTHelper.getDouble(compound, "look_target_x"), NBTHelper.getDouble(compound, "look_target_y"), NBTHelper.getDouble(compound, "look_target_z"));

		return null;
	}

	public void setLookTarget(Vec3d lookTarget) {
		NBTTagCompound compound = new NBTTagCompound();
		if (lookTarget == null) return;
		else {
			compound.setDouble("look_target_x", lookTarget.x);
			compound.setDouble("look_target_y", lookTarget.y);
			compound.setDouble("look_target_z", lookTarget.z);
		}

		this.getDataManager().set(DATA_LOOK_TARGET, compound);
		this.getDataManager().setDirty(DATA_LOOK_TARGET);
	}


	@Nullable
	public FairyData getDataFairy() {
		NBTTagCompound compound = this.getDataManager().get(DATA_FAIRY);
		return FairyData.deserialize(compound);
	}

	public void setDataFairy(FairyData fairy) {
		if (fairy == null) return;

		this.getDataManager().set(DATA_FAIRY, fairy.serializeNBT());
		this.getDataManager().setDirty(DATA_FAIRY);
	}

	public BlockPos getDataOrigin() {
		return this.getDataManager().get(DATA_ORIGIN);
	}

	public void setDataOrigin(BlockPos origin) {
		if (origin == null) return;

		this.getDataManager().set(DATA_ORIGIN, origin);
		this.getDataManager().setDirty(DATA_ORIGIN);
	}

	@Nonnull
	public ItemStack getDataHeldItem() {
		return this.getDataManager().get(DATA_HELD_ITEM);
	}

	public void setDataHeldItem(@Nonnull ItemStack stack) {
		this.getDataManager().set(DATA_HELD_ITEM, stack);
		this.getDataManager().setDirty(DATA_HELD_ITEM);
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

	public boolean isFlying() {
		return !this.onGround;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.getDataManager().register(DATA_FAIRY, new FairyData().serializeNBT());
		this.getDataManager().register(DATA_LOOK_TARGET, new NBTTagCompound());
		this.getDataManager().register(DATA_HELD_ITEM, ItemStack.EMPTY);
		this.getDataManager().register(DATA_ORIGIN, BlockPos.ORIGIN);
	}

	@Override
	public void initEntityAI() {
		FairyData dataFairy = getDataFairy();
		if (dataFairy != null && dataFairy.isDepressed) return;

		this.tasks.addTask(1, new EntityAIFollowOwnerFlying(this, 1.0D, 5.0F, 1.0F));
		this.tasks.addTask(2, new FairyAIWanderAvoidWaterFlying(this, 1.0D));
		this.tasks.addTask(3, new EntityAIFollow(this, 1.0D, 3.0F, 7.0F));

	}

	@Override
	protected void setupTamedAI() {
		FairyData dataFairy = getDataFairy();
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
		this.getAttributeMap().registerAttribute(EntityPlayer.REACH_DISTANCE);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1.0D);
		this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(RandUtil.nextDouble(2, 3));
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(1);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64);
		this.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(5);
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
			((EntityLivingBase) entity).motionY += 0.1;
			((EntityLivingBase) entity).setRevengeTarget(this);
		}
		entity.fallDistance = 0;

		//if (entity.world.isRemote)
		//	LibParticles.AIR_THROTTLE(world, getPositionVector(), entity, primaryColor, primaryColor.brighter(), -1);
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		setNoGravity(true);
		fallDistance = 0;

		FairyData dataFairy = getDataFairy();
		float size = dataFairy != null && dataFairy.wasTamperedWith ? 0.2f : 1f;
		setScale(size);

		//	if (isAIDisabled()) return;
		if (isDead) return;

		Vec3d lookTarget = getLookTarget();

		if (dataFairy != null && dataFairy.isDepressed) {

			if (lookTarget != null)
				getLookHelper().setLookPosition(lookTarget.x, lookTarget.y, lookTarget.z, 20, 20);

			//	setNoAI(false);

			if (!tasks.taskEntries.isEmpty()) {
				ArrayList<EntityAITasks.EntityAITaskEntry> tempTasks = new ArrayList<>(tasks.taskEntries);
				for (EntityAITasks.EntityAITaskEntry taskEntry : tempTasks) {
					tasks.removeTask(taskEntry.action);
				}
			}

			if (!targetTasks.taskEntries.isEmpty()) {
				ArrayList<EntityAITasks.EntityAITaskEntry> tempTargetTasks = new ArrayList<>(targetTasks.taskEntries);
				for (EntityAITasks.EntityAITaskEntry taskEntry : tempTargetTasks) {
					targetTasks.removeTask(taskEntry.action);
				}
			}
//			return;
		}

		if (fairyTask != null) {
			if (fairyTask.shouldTrigger(this)) {
				fairyTask.onTrigger(this);
			}
		}

		if (dataFairy != null && getNavigator().noPath())
			if (!dataFairy.isDepressed) {
				getMoveHelper().setMoveTo(posX + RandUtil.nextDouble(-32, 32), posY + RandUtil.nextDouble(-32, 32), posZ + RandUtil.nextDouble(-32, 32), 2);

			} else if (moving && moveTargetPos != null) {
				Minecraft.getMinecraft().player.sendChatMessage(animatingPos.toString());
				setPosition(animatingPos.x, animatingPos.y, animatingPos.z);
			}
	}

	public void moveTo(BlockPos pos) {

		Vec3d to = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
		Path path = getNavigator().getPathToXYZ(to.x, to.y, to.z);

		if (path != null) {

			Keyframe[] frames = new Keyframe[path.getCurrentPathLength()];
			for (int i = 0; i < path.getCurrentPathLength(); i++) {
				PathPoint pathPoint = path.getPathPointFromIndex(i);
				Vec3d node = new Vec3d(pathPoint.x + 0.5, pathPoint.y + 0.5, pathPoint.z + 0.5);

				frames[i] = new Keyframe((float) i / (float) path.getCurrentPathLength(), node, i == 0 || i == path.getCurrentPathLength() ? Easing.easeOutQuint : Easing.linear);
			}

			KeyframeAnimation<EntityFairy> animation = new KeyframeAnimation(this, "animatingPos");
			animation.setKeyframes(frames);
			animation.setDuration((float) (getPositionVector().distanceTo(to) * 10));
			animation.setCompletion(() -> moving = false);
			ANIMATOR.add(animation);
			moving = true;
		}

		moveTargetPos = pos;
		moving = true;
	}

	public boolean isMoving() {
		return moving;
	}

	public BlockPos getOriginPos() {
		return getDataOrigin();
	}

	@NotNull
	@Override
	public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand) {
		ItemStack heldItem = player.getHeldItemMainhand();
		if (heldItem.getItem() == ModItems.JAR_ITEM && heldItem.getItemDamage() == 0) {
			succFairy(heldItem, player);
			return EnumActionResult.SUCCESS;
		} else if (!heldItem.isEmpty() && heldItem.getItem() != ModItems.FAIRY_BELL) {
			FairyTask task = FairyTaskManager.INSTANCE.getTaskForItemStack(heldItem);
			if (task != null) fairyTask = task;
			playSound(ModSounds.POSITIVE_LIGHT_TWINKLE, 1f, RandUtil.nextFloat());
			heldItem.shrink(1);
			return EnumActionResult.SUCCESS;
		}
		return super.applyPlayerInteraction(player, vec, hand);
	}

	private void succFairy(ItemStack stack, EntityPlayer player) {
		FairyData dataFairy = getDataFairy();
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
		if (getIsInvulnerable()) return;

		if (!world.isRemote) {
			FairyData dataFairy = getDataFairy();
			if (dataFairy == null) return;

			if (dataFairy.isDepressed) {

				ItemStack stack = new ItemStack(ModItems.FAIRY_ITEM);
				NBTHelper.setTag(stack, "fairy", dataFairy.serializeNBT());

				world.removeEntity(this);

				EntityItem entityItem = new EntityItem(world);
				entityItem.setPosition(posX, posY, posZ);
				entityItem.setItem(stack);
				entityItem.setPickupDelay(20);
				entityItem.setNoDespawn();

				world.spawnEntity(entityItem);
				return;
			}

			super.onDeath(cause);

			if (getHealth() <= 0)
				PacketHandler.NETWORK.sendToAllAround(new PacketExplode(getPositionVector().add(0, 0.25, 0), dataFairy.primaryColor, dataFairy.secondaryColor, 0.5, 0.5, RandUtil.nextInt(100, 200), 75, 25, true),
						new NetworkRegistry.TargetPoint(world.provider.getDimension(), posX, posY, posZ, 256));
		}
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return ModSounds.SUBTLE_MAGIC_BOOK_GLINT;
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return ModSounds.SUBTLE_MAGIC_BOOK_GLINT;
	}

	@Override
	public void dropLoot(boolean wasRecentlyHit, int lootingModifier, @Nonnull DamageSource source) {
		//super.dropLoot(wasRecentlyHit, lootingModifier, source);
		FairyData dataFairy = getDataFairy();
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

		setDataFairy(FairyData.deserialize(NBTHelper.getCompoundTag(compound, "fairy")));

		if (NBTHelper.hasKey(compound, "held_item")) {
			NBTTagCompound heldItem = NBTHelper.getCompoundTag(compound, "held_item");
			if (heldItem != null)
				setDataHeldItem(new ItemStack(heldItem));
		}

		if (NBTHelper.hasKey(compound, "look_target_x") && NBTHelper.hasKey(compound, "look_target_y") && NBTHelper.hasKey(compound, "look_target_z"))
			setLookTarget(new Vec3d(NBTHelper.getDouble(compound, "look_target_x"), NBTHelper.getDouble(compound, "look_target_y"), NBTHelper.getDouble(compound, "look_target_z")));
		else setLookTarget(null);

		if (NBTHelper.hasKey(compound, "move_target_x") && NBTHelper.hasKey(compound, "move_target_y") && NBTHelper.hasKey(compound, "move_target_z"))
			moveTargetPos = new BlockPos(NBTHelper.getInteger(compound, "move_target_x"), NBTHelper.getInteger(compound, "move_target_y"), NBTHelper.getInteger(compound, "move_target_z"));

		if (NBTHelper.hasKey(compound, "origin_x") && NBTHelper.hasKey(compound, "origin_y") && NBTHelper.hasKey(compound, "origin_z"))
			setDataOrigin(new BlockPos(NBTHelper.getInteger(compound, "origin_x"), NBTHelper.getInteger(compound, "origin_y"), NBTHelper.getInteger(compound, "origin_z")));

		if (NBTHelper.hasKey(compound, "moving")) {
			moving = NBTHelper.getBoolean(compound, "moving", true);
		}

		if (NBTHelper.hasKey(compound, "fairy_task")) {
			fairyTask = FairyTaskManager.INSTANCE.getTaskFromKey("fairy_task");
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);

		FairyData dataFairy = getDataFairy();
		if (dataFairy != null)
			NBTHelper.setCompoundTag(compound, "fairy", dataFairy.serializeNBT());

		NBTTagCompound stackCompound = new NBTTagCompound();
		getDataHeldItem().writeToNBT(stackCompound);
		NBTHelper.setCompoundTag(compound, "held_item", stackCompound);

		Vec3d targetLook = getLookTarget();
		if (targetLook != null) {
			compound.setDouble("look_target_x", targetLook.x);
			compound.setDouble("look_target_y", targetLook.y);
			compound.setDouble("look_target_z", targetLook.z);
		}

		if (moveTargetPos != null) {
			compound.setInteger("move_target_x", moveTargetPos.getX());
			compound.setInteger("move_target_y", moveTargetPos.getY());
			compound.setInteger("move_target_z", moveTargetPos.getZ());
		}

		BlockPos origin = getDataOrigin();
		if (origin != null) {
			compound.setInteger("origin_x", origin.getX());
			compound.setInteger("origin_y", origin.getY());
			compound.setInteger("origin_z", origin.getZ());
		}

		compound.setBoolean("moving", moving);

		if (fairyTask != null) {
			NBTHelper.setString(compound, "fairy_task", fairyTask.getNBTKey());
		}
	}
}
