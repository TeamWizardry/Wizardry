package com.teamwizardry.wizardry.common.entity;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.teamwizardry.librarianlib.features.animator.Animator;
import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.NBTConstants.NBT;
import com.teamwizardry.wizardry.api.entity.fairy.FairyData;
import com.teamwizardry.wizardry.api.entity.fairy.fairytasks.FairyTaskController;
import com.teamwizardry.wizardry.api.entity.fairy.fairytasks.FairyTaskRegistry;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.entity.ai.FairyMoveHelper;
import com.teamwizardry.wizardry.common.entity.ai.WizardryFlyablePathNavigator;
import com.teamwizardry.wizardry.common.network.PacketExplode;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityFlying;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.teamwizardry.wizardry.api.entity.fairy.fairytasks.FairyTaskRegistry.IDLE_TASK;


/**
 * Created by Demoniaque on 8/21/2016.
 */
public class EntityFairy extends EntityTameable implements EntityFlying {
	private static final Animator ANIMATOR = new Animator();
	private static final DataParameter<ItemStack> DATA_HELD_ITEM = EntityDataManager.createKey(EntityFairy.class, DataSerializers.ITEM_STACK);
	private static final DataParameter<NBTTagCompound> DATA_FAIRY = EntityDataManager.createKey(EntityFairy.class, DataSerializers.COMPOUND_TAG);
	private static final DataParameter<NBTTagCompound> DATA_LOOK_TARGET = EntityDataManager.createKey(EntityFairy.class, DataSerializers.COMPOUND_TAG);
	private static final DataParameter<BlockPos> DATA_ORIGIN_BLOCK = EntityDataManager.createKey(EntityFairy.class, DataSerializers.BLOCK_POS);
	private static final DataParameter<BlockPos> DATA_TARGET_BLOCK = EntityDataManager.createKey(EntityFairy.class, DataSerializers.BLOCK_POS);
	private static final DataParameter<Optional<UUID>> DATA_ATTACHED_FAIRY = EntityDataManager.createKey(EntityFairy.class, DataSerializers.OPTIONAL_UNIQUE_ID);

	static {
		ANIMATOR.setUseWorldTicks(true);
	}

	private EntityAIAvoidEntity<EntityPlayer> avoidEntity;
	@Nonnull
	public final FairyTaskController fairyTaskController = new FairyTaskController();
	private boolean moving = false;
	@Nullable
	public Vec3d currentTarget = null;
	@Nullable
	public Vec3d currentOrigin = null;
	@Nullable
	private EnumFacing direction;
	private int steps;
	private double targetDeltaX;
	private double targetDeltaY;
	private double targetDeltaZ;

	public EntityFairy(World worldIn) {
		super(worldIn);
		setSize(1f, 1f);

		isAirBorne = true;
		experienceValue = 5;
		moveHelper = new FairyMoveHelper(this);

		setDataFairy(new FairyData());
		setLookTarget(null);
		this.direction = EnumFacing.UP;
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
		this.direction = EnumFacing.UP;
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

	public UUID getAttachedFairy() {
		return this.getDataManager().get(DATA_ATTACHED_FAIRY).orNull();
	}

	public void setAttachedFairy(UUID uuid) {
		this.getDataManager().set(DATA_ATTACHED_FAIRY, Optional.fromNullable(uuid));
		this.getDataManager().setDirty(DATA_ATTACHED_FAIRY);
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

	public BlockPos getDataOriginBlock() {
		return this.getDataManager().get(DATA_ORIGIN_BLOCK);
	}

	public void setDataOriginBlock(BlockPos origin) {
		this.getDataManager().set(DATA_ORIGIN_BLOCK, origin);
		this.getDataManager().setDirty(DATA_ORIGIN_BLOCK);
	}

	public BlockPos getDataTargetBlock() {
		return this.getDataManager().get(DATA_TARGET_BLOCK);
	}

	public void setDataTargetBlock(BlockPos origin) {
		this.getDataManager().set(DATA_TARGET_BLOCK, origin);
		this.getDataManager().setDirty(DATA_TARGET_BLOCK);
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
		this.getDataManager().register(DATA_ORIGIN_BLOCK, BlockPos.ORIGIN);
		this.getDataManager().register(DATA_TARGET_BLOCK, BlockPos.ORIGIN);
		this.getDataManager().register(DATA_ATTACHED_FAIRY, Optional.absent());
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

		if (dataFairy != null && dataFairy.isDepressed) {

			Vec3d lookTarget = getLookTarget();
			if (lookTarget != null && !moving)
				getLookHelper().setLookPosition(lookTarget.x, lookTarget.y, lookTarget.z, 20, 20);

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
		}

		if (!world.isRemote)
			fairyTaskController.tick(this);

		if (dataFairy != null && getNavigator().noPath())
			if (!dataFairy.isDepressed) {
				getMoveHelper().setMoveTo(posX + RandUtil.nextDouble(-32, 32), posY + RandUtil.nextDouble(-32, 32), posZ + RandUtil.nextDouble(-32, 32), 2);

			} else if (moving) {

				if (currentTarget == null) return;

				if (getPositionVector().distanceTo(currentTarget) < 0.5) {
					setPosition(currentTarget.x, currentTarget.y, currentTarget.z);
					motionX = 0;
					motionY = 0;
					motionZ = 0;

					this.targetDeltaX = 0.0D;
					this.targetDeltaY = 0.0D;
					this.targetDeltaZ = 0.0D;

					moving = false;
					currentTarget = null;
					return;
				}

				if (!world.isRemote) {
					this.targetDeltaX = MathHelper.clamp(this.targetDeltaX * 1.025D, -1.0D, 1.0D);
					this.targetDeltaY = MathHelper.clamp(this.targetDeltaY * 1.025D, -1.0D, 1.0D);
					this.targetDeltaZ = MathHelper.clamp(this.targetDeltaZ * 1.025D, -1.0D, 1.0D);
					this.motionX += (this.targetDeltaX - this.motionX) * 0.2D;
					this.motionY += (this.targetDeltaY - this.motionY) * 0.2D;
					this.motionZ += (this.targetDeltaZ - this.motionZ) * 0.2D;
				}

				this.setPosition(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
				ProjectileHelper.rotateTowardsMovement(this, 0.5F);

				if (this.world.isRemote) {
					this.world.spawnParticle(EnumParticleTypes.END_ROD, this.posX - this.motionX, this.posY - this.motionY + 0.15D, this.posZ - this.motionZ, 0.0D, 0.0D, 0.0D);
				} else if (this.currentTarget != null) {
					if (this.steps > 0) {
						--this.steps;

						if (this.steps == 0) {
							this.selectNextMoveDirection(this.direction == null ? null : this.direction.getAxis());
						}
					}

					if (this.direction != null) {
						BlockPos blockpos = new BlockPos(this);
						EnumFacing.Axis enumfacing$axis = this.direction.getAxis();

						if (this.world.isBlockNormalCube(blockpos.offset(this.direction), false)) {
							this.selectNextMoveDirection(enumfacing$axis);
						} else {
							BlockPos blockpos1 = new BlockPos(this.currentTarget);

							if (enumfacing$axis == EnumFacing.Axis.X && blockpos.getX() == blockpos1.getX() || enumfacing$axis == EnumFacing.Axis.Z && blockpos.getZ() == blockpos1.getZ() || enumfacing$axis == EnumFacing.Axis.Y && blockpos.getY() == blockpos1.getY()) {
								this.selectNextMoveDirection(enumfacing$axis);
							}
						}
					}
				}
			}
	}

	public void moveTo(@Nonnull BlockPos pos) {
		Vec3d to = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
		moveTo(to);
	}

	public void moveTo(@Nonnull Vec3d to) {
		currentTarget = to;
		currentOrigin = getPositionVector();
		moving = true;

		for (EnumFacing facing : EnumFacing.values()) {
			if (world.isAirBlock(getPosition().offset(facing))) {
				this.selectNextMoveDirection(facing.getAxis());
				return;
			}
		}
	}

	public boolean isMoving() {
		return moving;
	}

	public BlockPos getOriginPos() {
		return getDataOriginBlock();
	}

	private void setDirection(@Nullable EnumFacing directionIn) {
		this.direction = directionIn;
	}

	private void selectNextMoveDirection(@Nullable EnumFacing.Axis p_184569_1_) {
		double d0 = 0.5D;
		BlockPos blockpos;

		if (this.currentTarget == null) {
			blockpos = (new BlockPos(this)).down();
		} else {
			//	d0 = (double) this.target.height * 0.5D;
			blockpos = new BlockPos(currentTarget);
		}

		double d1 = (double) blockpos.getX() + 0.5D;
		double d2 = (double) blockpos.getY() + d0;
		double d3 = (double) blockpos.getZ() + 0.5D;
		EnumFacing enumfacing = null;

		if (blockpos.distanceSqToCenter(this.posX, this.posY, this.posZ) >= 4.0D) {
			BlockPos blockpos1 = new BlockPos(this);
			List<EnumFacing> list = Lists.newArrayList();

			if (p_184569_1_ != EnumFacing.Axis.X) {
				if (blockpos1.getX() < blockpos.getX() && this.world.isAirBlock(blockpos1.east())) {
					list.add(EnumFacing.EAST);
				} else if (blockpos1.getX() > blockpos.getX() && this.world.isAirBlock(blockpos1.west())) {
					list.add(EnumFacing.WEST);
				}
			}

			if (p_184569_1_ != EnumFacing.Axis.Y) {
				if (blockpos1.getY() < blockpos.getY() && this.world.isAirBlock(blockpos1.up())) {
					list.add(EnumFacing.UP);
				} else if (blockpos1.getY() > blockpos.getY() && this.world.isAirBlock(blockpos1.down())) {
					list.add(EnumFacing.DOWN);
				}
			}

			if (p_184569_1_ != EnumFacing.Axis.Z) {
				if (blockpos1.getZ() < blockpos.getZ() && this.world.isAirBlock(blockpos1.south())) {
					list.add(EnumFacing.SOUTH);
				} else if (blockpos1.getZ() > blockpos.getZ() && this.world.isAirBlock(blockpos1.north())) {
					list.add(EnumFacing.NORTH);
				}
			}

			enumfacing = EnumFacing.random(this.rand);

			if (list.isEmpty()) {
				for (int i = 5; !this.world.isAirBlock(blockpos1.offset(enumfacing)) && i > 0; --i) {
					enumfacing = EnumFacing.random(this.rand);
				}
			} else {
				enumfacing = list.get(this.rand.nextInt(list.size()));
			}

			d1 = this.posX + (double) enumfacing.getXOffset();
			d2 = this.posY + (double) enumfacing.getYOffset();
			d3 = this.posZ + (double) enumfacing.getZOffset();
		}

		this.setDirection(enumfacing);
		double d6 = d1 - this.posX;
		double d7 = d2 - this.posY;
		double d4 = d3 - this.posZ;
		double d5 = (double) MathHelper.sqrt(d6 * d6 + d7 * d7 + d4 * d4);

		if (d5 == 0.0D) {
			this.targetDeltaX = 0.0D;
			this.targetDeltaY = 0.0D;
			this.targetDeltaZ = 0.0D;
		} else {
			this.targetDeltaX = d6 / d5 * 0.15D;
			this.targetDeltaY = d7 / d5 * 0.15D;
			this.targetDeltaZ = d4 / d5 * 0.15D;
		}

		this.isAirBorne = true;
		this.steps = 10;
	}

	@NotNull
	@Override
	public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand) {
		ItemStack heldItem = player.getHeldItemMainhand();
		if (heldItem.getItem() == ModItems.JAR_ITEM && heldItem.getItemDamage() == 0) {
			succFairy(heldItem, player);
			return EnumActionResult.SUCCESS;
		} else if (!heldItem.isEmpty() && heldItem.getItem() != ModItems.FAIRY_BELL) {

			ResourceLocation task = FairyTaskRegistry.getAcceptableTask(heldItem, this);
			if (task != IDLE_TASK) {
				fairyTaskController.setTask(this, task);
				playSound(ModSounds.POSITIVE_LIGHT_TWINKLE, 1f, RandUtil.nextFloat());
				heldItem.shrink(1);
			} else playSound(ModSounds.NEGATIVELY_PITCHED_BREATHE_PUHH, 1, RandUtil.nextFloat());
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

		if (NBTHelper.hasKey(compound, "attached_fairy")) {
			setAttachedFairy(NBTHelper.getUniqueId(compound, "attached_fairy"));
		}

		if (NBTHelper.hasKey(compound, "look_target_x") && NBTHelper.hasKey(compound, "look_target_y") && NBTHelper.hasKey(compound, "look_target_z"))
			setLookTarget(new Vec3d(NBTHelper.getDouble(compound, "look_target_x"), NBTHelper.getDouble(compound, "look_target_y"), NBTHelper.getDouble(compound, "look_target_z")));
		else setLookTarget(null);

		if (NBTHelper.hasKey(compound, "current_target_x") && NBTHelper.hasKey(compound, "current_target_y") && NBTHelper.hasKey(compound, "current_target_z"))
			currentTarget = new Vec3d(NBTHelper.getDouble(compound, "current_target_x"), NBTHelper.getDouble(compound, "current_target_y"), NBTHelper.getDouble(compound, "current_target_z"));

		if (NBTHelper.hasKey(compound, "origin_x") && NBTHelper.hasKey(compound, "origin_y") && NBTHelper.hasKey(compound, "origin_z"))
			setDataOriginBlock(new BlockPos(NBTHelper.getInteger(compound, "origin_x"), NBTHelper.getInteger(compound, "origin_y"), NBTHelper.getInteger(compound, "origin_z")));

		if (NBTHelper.hasKey(compound, "target_x") && NBTHelper.hasKey(compound, "target_y") && NBTHelper.hasKey(compound, "target_z"))
			setDataTargetBlock(new BlockPos(NBTHelper.getInteger(compound, "target_x"), NBTHelper.getInteger(compound, "target_y"), NBTHelper.getInteger(compound, "target_z")));

		if (NBTHelper.hasKey(compound, "moving")) {
			moving = NBTHelper.getBoolean(compound, "moving", true);
		}

		if (NBTHelper.hasKey(compound, "fairy_task")) {
			String resource = NBTHelper.getString(compound, "fairy_task");
			if (resource != null)
				fairyTaskController.setTask(this, new ResourceLocation(resource));
		}

		if (this.direction != null) {
			compound.setInteger("direction", this.direction.getIndex());
		}

		if (compound.hasKey("direction", 99)) {
			this.direction = EnumFacing.byIndex(compound.getInteger("direction"));
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

		UUID uuid = getAttachedFairy();
		if (uuid != null) {
			NBTHelper.setUniqueId(compound, "attached_fairy", uuid);
		}

		Vec3d targetLook = getLookTarget();
		if (targetLook != null) {
			compound.setDouble("look_target_x", targetLook.x);
			compound.setDouble("look_target_y", targetLook.y);
			compound.setDouble("look_target_z", targetLook.z);
		}

		if (currentTarget != null) {
			compound.setDouble("current_target_x", currentTarget.x);
			compound.setDouble("current_target_y", currentTarget.y);
			compound.setDouble("current_target_z", currentTarget.z);
		}

		BlockPos origin = getDataOriginBlock();
		if (origin != null) {
			compound.setInteger("origin_x", origin.getX());
			compound.setInteger("origin_y", origin.getY());
			compound.setInteger("origin_z", origin.getZ());
		}

		BlockPos target = getDataTargetBlock();
		if (target != null) {
			compound.setInteger("target_x", target.getX());
			compound.setInteger("target_y", target.getY());
			compound.setInteger("target_z", target.getZ());
		}

		compound.setBoolean("moving", moving);

		NBTHelper.setString(compound, "fairy_task", fairyTaskController.getLocation().toString());
	}
}
